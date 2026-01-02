package com.develop.feature.profile.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("username") val username: String? = null,
    @SerialName("phone") val phone: String? = null
)
