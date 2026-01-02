package com.develop.feature.contacts.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindUsersByPhonesRequest(
    @SerialName("phones") val phones: List<String>
)
