package com.develop.feature.contacts.domain.model

data class AppContact(
    val id: Long,
    val userId: Long,
    val username: String?,
    val phone: String?,
    val displayName: String? = null
)
