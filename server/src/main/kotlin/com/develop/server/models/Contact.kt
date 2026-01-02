package com.develop.server.models

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val id: Long,
    val userId: Long,
    val username: String?,
    val phone: String?,
    val addedAt: Instant
)

@Serializable
data class AddContactRequest(
    val userId: Long,
    val mutual: Boolean
)

@Serializable
data class FindUsersByPhonesRequest(
    val phones: List<String>
)

@Serializable
data class FoundUser(
    val id: Long,
    val phone: String,
    val username: String?
)
