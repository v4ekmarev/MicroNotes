package com.develop.feature.profile.data.api

import com.develop.feature.profile.data.models.UpdateProfileRequest
import com.develop.feature.profile.data.models.UserProfileResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ProfileApi(private val client: HttpClient) {
    
    suspend fun getProfile(): Result<UserProfileResponse> {
        return runCatching {
            client.get("/api/users/me").body()
        }
    }
    
    suspend fun updateProfile(username: String?, phone: String?): Result<UserProfileResponse> {
        return runCatching {
            client.put("/api/users/me") {
                contentType(ContentType.Application.Json)
                setBody(UpdateProfileRequest(username, phone))
            }.body()
        }
    }
}
