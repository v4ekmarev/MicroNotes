package com.develop.server.models

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val deviceId: String,
    val username: String? = null,
    val phone: String? = null,
    val createdAt: Instant
)

@Serializable
data class DeviceAuthRequest(
    val deviceId: String? = null
)

@Serializable
data class DeviceAuthResponse(
    val token: String,
    val deviceId: String,
    val isNewUser: Boolean
)

@Serializable
data class UserSearchResult(
    val id: Long,
    val username: String?,
    val phone: String?
)

@Serializable
data class UpdateProfileRequest(
    val username: String? = null,
    val phone: String? = null
)
