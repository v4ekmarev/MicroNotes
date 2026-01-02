package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.UseCaseWithParams
import com.develop.feature.note.domain.model.NoteDetails

interface GetNoteDetailsUseCase : UseCaseWithParams<Long?, NoteDetails>
