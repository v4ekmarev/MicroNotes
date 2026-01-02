package com.develop.feature.contacts.domain.usecase

import com.develop.core.common.usecase.ResultUseCaseWithParams
import com.develop.feature.contacts.domain.model.AppContact

data class AddContactParams(
    val userId: Long,
    val mutual: Boolean
)

interface AddContactUseCase : ResultUseCaseWithParams<AddContactParams, AppContact>
