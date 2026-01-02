package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.ResultUseCase
import com.develop.feature.note.domain.model.SharedNote

/**
 * UseCase для получения входящих заметок с сервера.
 */
interface GetInboxUseCase : ResultUseCase<List<SharedNote>>
