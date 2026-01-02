package com.develop.feature.note.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendNoteResponse(
    @SerialName("id") val id: Long,
    @SerialName("recipientId") val recipientId: Long,
    @SerialName("recipientUsername") val recipientUsername: String?
)
