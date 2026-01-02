package com.develop.feature.contacts.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.model.AppContact

class GetAppContactsUseCaseImpl(
    private val repository: ContactsRepository
) : GetAppContactsUseCase {
    
    override suspend fun execute(): Result<List<AppContact>> {
        return repository.getAppContacts()
    }
}
