package com.develop.feature.contacts.domain.usecase

class ContactsUseCases(
    val getAppContacts: GetAppContactsUseCase,
    val addContact: AddContactUseCase,
    val removeContact: RemoveContactUseCase,
    val findUsersFromPhoneContacts: FindUsersFromPhoneContactsUseCase,
    val getInviteLink: GetInviteLinkUseCase,
    val getUserById: GetUserByIdUseCase
)
