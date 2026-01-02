package com.develop.feature.contacts.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoundUserResponse(
    @SerialName("id") val id: Long,
    @SerialName("phone") val phone: String,
    @SerialName("username") val username: String?
)
