package com.develop.feature.contacts.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.model.AppContact

class GetUserByIdUseCaseImpl(
    private val repository: ContactsRepository
) : GetUserByIdUseCase {
    
    override suspend fun execute(params: Long): Result<AppContact?> {
        return repository.getUserById(params)
    }
}
