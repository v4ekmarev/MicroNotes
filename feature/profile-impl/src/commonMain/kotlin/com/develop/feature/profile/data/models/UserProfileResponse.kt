package com.develop.feature.profile.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("id") val id: Long,
    @SerialName("username") val username: String?,
    @SerialName("phone") val phone: String?
)
