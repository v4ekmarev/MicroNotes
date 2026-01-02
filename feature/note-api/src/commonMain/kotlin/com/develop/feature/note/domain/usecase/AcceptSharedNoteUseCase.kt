package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.ResultUseCaseWithParams
import com.develop.feature.note.domain.model.SharedNote

/**
 * UseCase для принятия входящей заметки:
 * 1. Сохраняет заметку в локальную БД с пометкой isShared=true
 * 2. Отправляет ACK на сервер для удаления заметки
 */
interface AcceptSharedNoteUseCase : ResultUseCaseWithParams<SharedNote, Unit>
