package com.develop.data.network.api

import com.develop.core.common.AppLogger
import com.develop.data.network.logging.CurlLoggingPlugin
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val TAG = "HttpClient"

expect fun createPlatformHttpClient(): HttpClient

class HttpClientFactory(
    private val tokenProvider: TokenProvider,
    private val isDebug: Boolean = false
) {
    fun create(baseUrl: String): HttpClient {
        return createPlatformHttpClient().config {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            install(WebSockets)
            
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenProvider.getToken()?.let { BearerTokens(it, "") }
                    }
                    refreshTokens {
                        // Пока без refresh токенов
                        null
                    }
                }
            }
            
            defaultRequest {
                url(baseUrl)
            }
            
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        AppLogger.d(TAG, message)
                    }
                }
                level = LogLevel.ALL
            }
            
            if (isDebug) {
                install(CurlLoggingPlugin)
            }
        }
    }
}

interface TokenProvider {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}

interface DeviceIdProvider {
    suspend fun getDeviceId(): String?
    suspend fun saveDeviceId(deviceId: String)
}
