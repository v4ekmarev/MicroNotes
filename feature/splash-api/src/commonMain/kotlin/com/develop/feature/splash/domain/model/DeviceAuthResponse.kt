package com.develop.feature.splash.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceAuthResponse(
    @SerialName("token") val token: String,
    @SerialName("deviceId") val deviceId: String,
    @SerialName("isNewUser") val isNewUser: Boolean
)
