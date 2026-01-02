package com.develop.feature.contacts.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSearchResponse(
    @SerialName("id") val id: Long,
    @SerialName("username") val username: String?,
    @SerialName("phone") val phone: String?
)
