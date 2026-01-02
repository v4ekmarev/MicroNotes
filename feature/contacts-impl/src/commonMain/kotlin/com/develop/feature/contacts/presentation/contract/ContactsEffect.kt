package com.develop.feature.contacts.presentation.contract

sealed interface ContactsEffect {
    data class ShareLink(val link: String) : ContactsEffect
    data object ContactAdded : ContactsEffect
    data object ContactRemoved : ContactsEffect
}
