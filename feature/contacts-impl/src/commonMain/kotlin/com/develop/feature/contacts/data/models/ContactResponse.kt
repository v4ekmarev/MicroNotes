package com.develop.feature.contacts.data.models

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactResponse(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("username") val username: String?,
    @SerialName("phone") val phone: String?,
    @SerialName("addedAt") val addedAt: Instant
)
