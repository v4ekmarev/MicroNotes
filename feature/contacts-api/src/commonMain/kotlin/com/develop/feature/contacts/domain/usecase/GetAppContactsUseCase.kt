package com.develop.feature.contacts.domain.usecase

import com.develop.core.common.usecase.ResultUseCase
import com.develop.feature.contacts.domain.model.AppContact

interface GetAppContactsUseCase : ResultUseCase<List<AppContact>>
