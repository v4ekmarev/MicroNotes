package com.develop.feature.contacts.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.model.AppContact
import com.develop.feature.contacts.domain.model.PhoneContact

class FindUsersFromPhoneContactsUseCaseImpl(
    private val repository: ContactsRepository
) : FindUsersFromPhoneContactsUseCase {
    
    override suspend fun execute(params: List<PhoneContact>): Result<List<AppContact>> {
        return repository.findUsersFromPhoneContacts(params)
    }
}
