package com.develop.feature.profile.presentation.contract

sealed interface ProfileEffect {
    data object ProfileSaved : ProfileEffect
}
