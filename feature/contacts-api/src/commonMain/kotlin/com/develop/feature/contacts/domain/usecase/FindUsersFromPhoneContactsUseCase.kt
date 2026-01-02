package com.develop.feature.contacts.domain.usecase

import com.develop.core.common.usecase.ResultUseCaseWithParams
import com.develop.feature.contacts.domain.model.AppContact
import com.develop.feature.contacts.domain.model.PhoneContact

interface FindUsersFromPhoneContactsUseCase : ResultUseCaseWithParams<List<PhoneContact>, List<AppContact>>
