package com.develop.feature.contacts.presentation.contract

import com.develop.feature.contacts.domain.model.AppContact

data class ContactsState(
    val isLoading: Boolean = false,
    val appContacts: List<AppContact> = emptyList(),
    val phoneContactsInApp: List<AppContact> = emptyList(),
    val inviteLink: String? = null,
    val inviteUser: AppContact? = null,
    val showInviteDialog: Boolean = false,
    val error: String? = null,
    val selectedTab: ContactsTab = ContactsTab.MY_CONTACTS
)

enum class ContactsTab {
    MY_CONTACTS,
    FIND_FRIENDS
}
