package com.develop.feature.note.domain.usecase

import com.develop.core.model.Note
import com.develop.feature.note.data.api.NoteSharingApi
import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.model.SharedNote
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AcceptSharedNoteUseCaseImpl(
    private val repository: NoteRepository,
    private val api: NoteSharingApi
) : AcceptSharedNoteUseCase {
    
    @OptIn(ExperimentalTime::class)
    override suspend fun execute(params: SharedNote): Result<Unit> {
        return runCatching {
            // 1. Сохраняем заметку в локальную БД
            val now = Clock.System.now().toEpochMilliseconds()
            val note = Note(
                title = params.title,
                content = params.content,
                isShared = true,
                senderName = params.senderUsername ?: params.senderPhone ?: "Unknown",
                createdAt = params.createdAt.toEpochMilliseconds(),
                updatedAt = now,
                sortOrder = now
            )
            repository.save(note)
            
            // 2. Отправляем ACK на сервер
            api.acknowledgeReceived(params.id).getOrThrow()
        }
    }
}
