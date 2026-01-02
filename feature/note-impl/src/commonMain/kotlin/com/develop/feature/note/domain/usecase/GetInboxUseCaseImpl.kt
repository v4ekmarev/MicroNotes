package com.develop.feature.note.domain.usecase

import com.develop.feature.note.data.api.NoteSharingApi
import com.develop.feature.note.domain.model.SharedNote

class GetInboxUseCaseImpl(
    private val api: NoteSharingApi
) : GetInboxUseCase {
    
    override suspend fun execute(): Result<List<SharedNote>> {
        return api.getInbox().map { responses ->
            responses.map { response ->
                SharedNote(
                    id = response.id,
                    senderId = response.senderId,
                    senderUsername = response.senderUsername,
                    senderPhone = response.senderPhone,
                    title = response.title,
                    content = response.content,
                    createdAt = response.createdAt
                )
            }
        }
    }
}
