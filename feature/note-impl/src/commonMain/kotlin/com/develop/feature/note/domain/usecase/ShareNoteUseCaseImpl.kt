package com.develop.feature.note.domain.usecase

import com.develop.feature.note.data.api.NoteSharingApi

class ShareNoteUseCaseImpl(
    private val noteSharingApi: NoteSharingApi
) : ShareNoteUseCase {
    override suspend fun execute(params: ShareNoteParams): Result<Unit> {
        return noteSharingApi.sendNoteToMany(
            recipientIds = params.recipientIds,
            title = params.title,
            content = params.content
        ).map { }
    }
}
