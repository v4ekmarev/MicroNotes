package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.UseCaseWithParams
import com.develop.core.model.Note

interface GetNoteUseCase : UseCaseWithParams<Long, Note?>
