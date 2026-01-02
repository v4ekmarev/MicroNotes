package com.develop.feature.profile.presentation.contract

import com.develop.feature.profile.domain.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val isEditing: Boolean = false,
    val editUsername: String = "",
    val editPhone: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)
