package com.develop.feature.splash.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceAuthRequest(
    @SerialName("deviceId") val deviceId: String? = null
)
