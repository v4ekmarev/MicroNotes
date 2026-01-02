package com.develop.feature.contacts.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddContactRequest(
    @SerialName("userId") val userId: Long,
    @SerialName("mutual") val mutual: Boolean
)
