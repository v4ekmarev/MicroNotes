package com.develop.feature.profile.domain.usecase

import com.develop.core.common.usecase.ResultUseCase
import com.develop.feature.profile.domain.model.UserProfile

interface GetProfileUseCase : ResultUseCase<UserProfile>
