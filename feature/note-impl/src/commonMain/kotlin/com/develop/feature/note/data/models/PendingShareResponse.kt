package com.develop.feature.note.data.models

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingShareResponse(
    @SerialName("id") val id: Long,
    @SerialName("senderId") val senderId: Long,
    @SerialName("senderUsername") val senderUsername: String?,
    @SerialName("senderPhone") val senderPhone: String?,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
    @SerialName("createdAt") val createdAt: Instant
)
