package com.develop.feature.contacts.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.model.AppContact

class AddContactUseCaseImpl(
    private val repository: ContactsRepository
) : AddContactUseCase {
    
    override suspend fun execute(params: AddContactParams): Result<AppContact> {
        return repository.addContact(params.userId, params.mutual)
    }
}
