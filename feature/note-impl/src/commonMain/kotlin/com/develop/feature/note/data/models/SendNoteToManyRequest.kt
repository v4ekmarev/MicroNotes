package com.develop.feature.note.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendNoteToManyRequest(
    @SerialName("recipientIds") val recipientIds: List<Long>,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String
)
