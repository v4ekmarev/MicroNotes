package com.develop.feature.contacts.presentation.contract

sealed interface ContactsAction {
    data object LoadContacts : ContactsAction
    data object LoadPhoneContacts : ContactsAction
    data object ShareInviteLink : ContactsAction
    data class AddContact(val userId: Long) : ContactsAction
    data class RemoveContact(val contactId: Long) : ContactsAction
    data class SelectTab(val tab: ContactsTab) : ContactsAction
    data object DismissInviteDialog : ContactsAction
    data object ConfirmAddInviteUser : ContactsAction
    data object DismissError : ContactsAction
}
