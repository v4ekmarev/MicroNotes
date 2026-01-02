package com.develop.feature.profile.presentation

import com.develop.feature.profile.presentation.contract.ProfileAction
import com.develop.feature.profile.presentation.contract.ProfileEffect
import com.develop.feature.profile.presentation.contract.ProfileState

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.feature.profile.domain.usecase.ProfileUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val useCases: ProfileUseCases
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect = _effect.asSharedFlow()
    
    init {
        loadProfile()
    }
    
    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> loadProfile()
            ProfileAction.StartEditing -> startEditing()
            ProfileAction.CancelEditing -> cancelEditing()
            ProfileAction.SaveProfile -> saveProfile()
            is ProfileAction.UpdateUsername -> updateUsername(action.username)
            is ProfileAction.UpdatePhone -> updatePhone(action.phone)
            ProfileAction.DismissError -> dismissError()
        }
    }
    
    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            useCases.getProfile.execute()
                .onSuccess { profile ->
                    _state.update { 
                        it.copy(
                            profile = profile, 
                            isLoading = false,
                            editUsername = profile.username.orEmpty(),
                            editPhone = profile.phone.orEmpty()
                        ) 
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
    
    private fun startEditing() {
        val profile = _state.value.profile ?: return
        _state.update { 
            it.copy(
                isEditing = true,
                editUsername = profile.username.orEmpty(),
                editPhone = profile.phone.orEmpty()
            ) 
        }
    }
    
    private fun cancelEditing() {
        val profile = _state.value.profile
        _state.update { 
            it.copy(
                isEditing = false,
                editUsername = profile?.username.orEmpty(),
                editPhone = profile?.phone.orEmpty()
            ) 
        }
    }
    
    private fun saveProfile() {
        val currentState = _state.value
        if (currentState.isSaving) return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            
            val username = currentState.editUsername.takeIf { it.isNotBlank() }
            val phone = currentState.editPhone.takeIf { it.isNotBlank() }
            
            useCases.updateProfile.execute(username, phone)
                .onSuccess { profile ->
                    _state.update { 
                        it.copy(
                            profile = profile, 
                            isEditing = false, 
                            isSaving = false,
                            editUsername = profile.username.orEmpty(),
                            editPhone = profile.phone.orEmpty()
                        ) 
                    }
                    _effect.emit(ProfileEffect.ProfileSaved)
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message, isSaving = false) }
                }
        }
    }
    
    private fun updateUsername(username: String) {
        _state.update { it.copy(editUsername = username) }
    }
    
    private fun updatePhone(phone: String) {
        _state.update { it.copy(editPhone = phone) }
    }
    
    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
