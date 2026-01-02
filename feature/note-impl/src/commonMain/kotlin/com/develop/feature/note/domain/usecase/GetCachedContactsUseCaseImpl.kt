package com.develop.feature.note.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.model.AppContact

class GetCachedContactsUseCaseImpl(
    private val contactsRepository: ContactsRepository
) : GetCachedContactsUseCase {
    override suspend fun execute(): List<AppContact> {
        return contactsRepository.getCachedContacts()
    }
}
