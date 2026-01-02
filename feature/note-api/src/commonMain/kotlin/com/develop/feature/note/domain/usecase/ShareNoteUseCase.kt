package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.ResultUseCaseWithParams

data class ShareNoteParams(
    val recipientIds: List<Long>,
    val title: String,
    val content: String
)

interface ShareNoteUseCase : ResultUseCaseWithParams<ShareNoteParams, Unit>
