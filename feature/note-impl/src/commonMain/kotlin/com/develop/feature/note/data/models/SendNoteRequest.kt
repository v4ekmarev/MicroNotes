package com.develop.feature.note.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendNoteRequest(
    @SerialName("recipientId") val recipientId: Long,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String
)
