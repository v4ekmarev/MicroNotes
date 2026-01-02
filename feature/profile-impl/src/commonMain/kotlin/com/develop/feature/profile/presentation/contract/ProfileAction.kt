package com.develop.feature.profile.presentation.contract

sealed interface ProfileAction {
    data object LoadProfile : ProfileAction
    data object StartEditing : ProfileAction
    data object CancelEditing : ProfileAction
    data object SaveProfile : ProfileAction
    data class UpdateUsername(val username: String) : ProfileAction
    data class UpdatePhone(val phone: String) : ProfileAction
    data object DismissError : ProfileAction
}
