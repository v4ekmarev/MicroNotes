package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.UseCase
import com.develop.core.model.Note

interface GetAllNotesUseCase : UseCase<List<Note>>
