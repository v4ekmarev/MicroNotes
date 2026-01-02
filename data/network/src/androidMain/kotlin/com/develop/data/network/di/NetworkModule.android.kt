package com.develop.data.network.di

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.develop.data.network.api.DeviceIdProvider
import com.develop.data.network.api.TokenProvider
import com.develop.core.common.Context as AppContext
import org.koin.core.module.Module
import org.koin.dsl.module
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

actual fun getBaseUrl(): String = "http://192.168.0.116:8080"
//actual fun getBaseUrl(): String = "http://192.168.0.96:8080"

actual fun isDebug(): Boolean = com.develop.data.network.BuildConfig.DEBUG

actual val networkPlatformModule: Module = module {
    single<SharedPreferences> {
        get<AppContext>().getSharedPreferences("micronotes_auth", Context.MODE_PRIVATE)
    }
    
    single<TokenProvider> { AndroidTokenProvider(get()) }
    single<DeviceIdProvider> { AndroidDeviceIdProvider(get()) }
}

class AndroidTokenProvider(
    private val prefs: SharedPreferences
) : TokenProvider {
    
    override suspend fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    override suspend fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    override suspend fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
    
    companion object {
        private const val KEY_TOKEN = "jwt_token"
    }
}

class AndroidDeviceIdProvider(
    private val prefs: SharedPreferences
) : DeviceIdProvider {
    
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    
    override suspend fun getDeviceId(): String? {
        val encryptedData = prefs.getString(KEY_DEVICE_ID, null) ?: return null
        val iv = prefs.getString(KEY_DEVICE_ID_IV, null) ?: return null
        
        return try {
            decrypt(
                Base64.decode(encryptedData, Base64.DEFAULT),
                Base64.decode(iv, Base64.DEFAULT)
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveDeviceId(deviceId: String) {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val encryptedData = cipher.doFinal(deviceId.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv
        
        prefs.edit()
            .putString(KEY_DEVICE_ID, Base64.encodeToString(encryptedData, Base64.DEFAULT))
            .putString(KEY_DEVICE_ID_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
    }
    
    private fun decrypt(encryptedData: ByteArray, iv: ByteArray): String {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(encryptedData), Charsets.UTF_8)
    }
    
    private fun getOrCreateSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }
        
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return keyGenerator.generateKey()
    }
    
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "micronotes_device_id_key"
        private const val KEY_DEVICE_ID = "encrypted_device_id"
        private const val KEY_DEVICE_ID_IV = "device_id_iv"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
