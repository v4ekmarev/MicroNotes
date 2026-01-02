package com.develop.feature.splash.domain.usecase

import com.develop.core.common.usecase.ResultUseCase
import com.develop.feature.splash.domain.model.DeviceAuthResponse

interface AuthenticateUseCase : ResultUseCase<DeviceAuthResponse>
