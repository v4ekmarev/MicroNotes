package com.develop.feature.splash.presentation.contract

sealed interface SplashEffect {
    data object NavigateToMain : SplashEffect
    data class ShowError(val message: String) : SplashEffect
}
