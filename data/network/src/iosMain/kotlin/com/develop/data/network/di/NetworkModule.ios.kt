package com.develop.data.network.di

import com.develop.data.network.api.DeviceIdProvider
import com.develop.data.network.api.TokenProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.CoreFoundation.CFDictionaryRef
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
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

@OptIn(ExperimentalForeignApi::class)
class IosDeviceIdProvider : DeviceIdProvider {
    
    override suspend fun getDeviceId(): String? {
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to SERVICE_NAME,
            kSecAttrAccount to ACCOUNT_NAME,
            kSecReturnData to true,
            kSecMatchLimit to kSecMatchLimitOne
        )
        
        return memScoped {
            val result = alloc<CFDictionaryRef?>()
            val status: OSStatus = SecItemCopyMatching(
                query.toCFDictionary(),
                result.ptr.reinterpret()
            )
            
            if (status == errSecSuccess) {
                val data = CFBridgingRelease(result.value) as? NSData
                data?.let {
                    NSString.create(data = it, encoding = NSUTF8StringEncoding) as? String
                }
            } else {
                null
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
        
        var status: OSStatus = SecItemCopyMatching(query.toCFDictionary(), null)
        
        if (status == errSecItemNotFound) {
            SecItemAdd(attributes.toCFDictionary(), null)
        } else if (status == errSecSuccess) {
            val updateAttributes = mapOf<Any?, Any?>(
                kSecValueData to data
            )
            SecItemUpdate(query.toCFDictionary(), updateAttributes.toCFDictionary())
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
