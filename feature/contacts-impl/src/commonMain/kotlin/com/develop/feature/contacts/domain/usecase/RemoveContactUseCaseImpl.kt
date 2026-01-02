package com.develop.feature.contacts.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository

class RemoveContactUseCaseImpl(
    private val repository: ContactsRepository
) : RemoveContactUseCase {
    
    override suspend fun execute(params: Long): Result<Unit> {
        return repository.removeContact(params)
    }
}
