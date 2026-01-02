package com.develop.feature.note.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InboxCountResponse(
    @SerialName("count") val count: Long
)
