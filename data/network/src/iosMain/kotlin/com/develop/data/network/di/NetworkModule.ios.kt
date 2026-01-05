package com.develop.data.network.di

import com.develop.data.network.api.DeviceIdProvider
import com.develop.data.network.api.TokenProvider
import com.develop.core.common.AppLogger
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIDevice
import platform.Foundation.NSUUID
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.CoreFoundation.kCFBooleanTrue
import platform.darwin.OSStatus

actual fun getBaseUrl(): String = "http://localhost:8080"

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
actual fun isDebug(): Boolean = Platform.isDebugBinary

actual val networkPlatformModule: Module = module {
    single<TokenProvider> { IosTokenProvider() }
    single<DeviceIdProvider> { IosDeviceIdProvider() }
}

class IosTokenProvider : TokenProvider {
    private val defaults = NSUserDefaults.standardUserDefaults
    
    override suspend fun getToken(): String? {
        return defaults.stringForKey(KEY_TOKEN)
    }
    
    override suspend fun saveToken(token: String) {
        defaults.setObject(token, KEY_TOKEN)
    }
    
    override suspend fun clearToken() {
        defaults.removeObjectForKey(KEY_TOKEN)
    }
    
    companion object {
        private const val KEY_TOKEN = "micronotes_jwt_token"
    }
}

/**
 * iOS реализация DeviceIdProvider с использованием Keychain.
 * 
 * Device ID сохраняется в iOS Keychain и переживает удаление/переустановку приложения.
 * Это позволяет идентифицировать пользователя даже после переустановки.
 * 
 * Используется [kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly] для баланса
 * безопасности и удобства - данные доступны после первой разблокировки устройства.
 * 
 * Приоритет генерации Device ID:
 * 1. Существующий ID из Keychain
 * 2. [UIDevice.identifierForVendor] (уникальный для vendor)
 * 3. [NSUUID] (случайный UUID)
 * 
 * Все операции логируются с префиксом [IosDeviceIdProvider] для отладки.
 */
@OptIn(ExperimentalForeignApi::class)
class IosDeviceIdProvider : DeviceIdProvider {
    
    @OptIn(BetaInteropApi::class)
    override suspend fun getDeviceId(): String? {
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to SERVICE_NAME,
            kSecAttrAccount to ACCOUNT_NAME,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )
        
        return memScoped {
            val result = alloc<CFTypeRefVar>()
            val status: OSStatus = SecItemCopyMatching(
                query.toCFDictionary(),
                result.ptr
            )
            
            when (status) {
                errSecSuccess -> {
                    val data = CFBridgingRelease(result.value) as? NSData
                    data?.let {
                        NSString.create(data = it, encoding = NSUTF8StringEncoding) as? String
                    }
                }
                errSecItemNotFound -> {
                    // Если в Keychain нет, генерируем новый ID и сохраняем
                    AppLogger.d("IosDeviceIdProvider", "Device ID not found in Keychain, generating new one")
                    val newDeviceId = UIDevice.currentDevice.identifierForVendor?.UUIDString 
                        ?: NSUUID().UUIDString
                    saveDeviceId(newDeviceId)
                    newDeviceId
                }
                else -> {
                    AppLogger.e("IosDeviceIdProvider", "Error reading from Keychain: status=$status")
                    null
                }
            }
        }
    }
    
    override suspend fun saveDeviceId(deviceId: String) {
        val data = (deviceId as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to SERVICE_NAME,
            kSecAttrAccount to ACCOUNT_NAME
        )
        
        val attributes = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to SERVICE_NAME,
            kSecAttrAccount to ACCOUNT_NAME,
            kSecValueData to data,
            kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        )

        val status: OSStatus = SecItemCopyMatching(query.toCFDictionary(), null)

        when (status) {
            errSecItemNotFound -> {
                val addStatus = SecItemAdd(attributes.toCFDictionary(), null)
                if (addStatus != errSecSuccess) {
                    AppLogger.e("IosDeviceIdProvider", "Error adding to Keychain: status=$addStatus")
                }
            }
            errSecSuccess -> {
                val updateAttributes = mapOf<Any?, Any?>(
                    kSecValueData to data
                )
                val updateStatus = SecItemUpdate(query.toCFDictionary(), updateAttributes.toCFDictionary())
                if (updateStatus != errSecSuccess) {
                    AppLogger.e("IosDeviceIdProvider", "Error updating Keychain: status=$updateStatus")
                }
            }
            else -> {
                AppLogger.e("IosDeviceIdProvider", "Error checking Keychain: status=$status")
            }
        }
    }
    
    private fun Map<Any?, Any?>.toCFDictionary(): CFDictionaryRef {
        @Suppress("UNCHECKED_CAST")
        return CFBridgingRetain(this as Map<Any, Any>) as CFDictionaryRef
    }
    
    companion object {
        private const val SERVICE_NAME = "com.develop.micronotes"
        private const val ACCOUNT_NAME = "device_id"
    }
}
