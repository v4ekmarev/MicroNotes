package com.develop.feature.splash.data.api

import com.develop.feature.splash.domain.model.DeviceAuthRequest
import com.develop.feature.splash.domain.model.DeviceAuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(private val client: HttpClient) {
    
    suspend fun authenticateDevice(deviceId: String?): Result<DeviceAuthResponse> {
        return runCatching {
            client.post("/api/auth/device") {
                contentType(ContentType.Application.Json)
                setBody(DeviceAuthRequest(deviceId))
            }.body()
        }
    }
}
