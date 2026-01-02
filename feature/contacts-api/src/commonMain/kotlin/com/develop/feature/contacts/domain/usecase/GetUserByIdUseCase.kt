package com.develop.feature.contacts.domain.usecase

import com.develop.core.common.usecase.ResultUseCaseWithParams
import com.develop.feature.contacts.domain.model.AppContact

interface GetUserByIdUseCase : ResultUseCaseWithParams<Long, AppContact?>
