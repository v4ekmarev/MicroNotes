package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.UseCaseWithParams
import com.develop.core.model.Note

interface SaveNoteUseCase : UseCaseWithParams<Note, Unit>
