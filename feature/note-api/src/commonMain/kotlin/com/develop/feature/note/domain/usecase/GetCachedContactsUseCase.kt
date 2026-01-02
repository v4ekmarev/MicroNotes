package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.UseCase
import com.develop.feature.contacts.domain.model.AppContact

interface GetCachedContactsUseCase : UseCase<List<AppContact>>
