package com.develop.feature.contacts.domain.usecase

import com.develop.feature.contacts.domain.ContactsRepository

class GetInviteLinkUseCaseImpl(
    private val repository: ContactsRepository
) : GetInviteLinkUseCase {
    
    override suspend fun execute(): Result<String> {
        return repository.getInviteLink()
    }
}
