package com.develop.feature.contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.feature.contacts.route.ContactsRoute
import com.develop.feature.contacts.domain.usecase.AddContactParams
import com.develop.feature.contacts.domain.usecase.ContactsUseCases
import com.develop.feature.contacts.presentation.contract.ContactsAction
import com.develop.feature.contacts.presentation.contract.ContactsEffect
import com.develop.feature.contacts.presentation.contract.ContactsState
import com.develop.feature.contacts.presentation.contract.ContactsTab
import com.develop.feature.contacts.domain.PhoneContactsProvider
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.contacts.CONTACTS
import dev.icerock.moko.permissions.contacts.READ_CONTACTS
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val useCases: ContactsUseCases,
    private val phoneContactsProvider: PhoneContactsProvider,
    private val permissionsController: PermissionsController,
    private val route: ContactsRoute
) : ViewModel() {
    
    private val _state = MutableStateFlow(ContactsState())
    val state: StateFlow<ContactsState> = _state.asStateFlow()
    
    private val _effect = MutableSharedFlow<ContactsEffect>()
    val effect = _effect.asSharedFlow()
    
    init {
        loadContacts()
        loadInviteLink()
        
        route.inviteUserId?.let { userId ->
            loadInviteUser(userId)
        }
    }
    
    fun onAction(action: ContactsAction) {
        when (action) {
            ContactsAction.LoadContacts -> loadContacts()
            ContactsAction.LoadPhoneContacts -> loadPhoneContacts()
            ContactsAction.ShareInviteLink -> shareInviteLink()
            is ContactsAction.AddContact -> addContact(action.userId, mutual = false)
            is ContactsAction.RemoveContact -> removeContact(action.contactId)
            is ContactsAction.SelectTab -> selectTab(action.tab)
            ContactsAction.DismissInviteDialog -> dismissInviteDialog()
            ContactsAction.ConfirmAddInviteUser -> confirmAddInviteUser()
            ContactsAction.DismissError -> dismissError()
        }
    }
    
    private fun loadContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            useCases.getAppContacts.execute()
                .onSuccess { contacts ->
                    _state.update { it.copy(appContacts = contacts, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
    
    private fun loadPhoneContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                delay(1000)
                 permissionsController.providePermission(Permission.READ_CONTACTS)
                
                val phoneContacts = phoneContactsProvider.getPhoneContacts()
                useCases.findUsersFromPhoneContacts.execute(phoneContacts)
                    .onSuccess { usersInApp ->
                        _state.update { it.copy(phoneContactsInApp = usersInApp, isLoading = false) }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error.message, isLoading = false) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Нет доступа к контактам", isLoading = false) }
            }
        }
    }
    
    private fun loadInviteLink() {
        viewModelScope.launch {
            useCases.getInviteLink.execute()
                .onSuccess { link ->
                    _state.update { it.copy(inviteLink = link) }
                }
                .onFailure {
                    _state.update { it }

                }
        }
    }
    
    private fun loadInviteUser(userId: Long) {
        viewModelScope.launch {
            useCases.getUserById.execute(userId)
                .onSuccess { user ->
                    if (user != null) {
                        _state.update { it.copy(inviteUser = user, showInviteDialog = true) }
                    }
                }
        }
    }
    
    private fun shareInviteLink() {
        val link = _state.value.inviteLink ?: return
        viewModelScope.launch {
            _effect.emit(ContactsEffect.ShareLink(link))
        }
    }
    
    private fun addContact(userId: Long, mutual: Boolean) {
        viewModelScope.launch {
            useCases.addContact.execute(AddContactParams(userId, mutual))
                .onSuccess {
                    _effect.emit(ContactsEffect.ContactAdded)
                    loadContacts()
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }
    
    private fun removeContact(contactId: Long) {
        viewModelScope.launch {
            useCases.removeContact.execute(contactId)
                .onSuccess {
                    _effect.emit(ContactsEffect.ContactRemoved)
                    loadContacts()
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }
    
    private fun selectTab(tab: ContactsTab) {
        _state.update { it.copy(selectedTab = tab) }
        if (tab == ContactsTab.FIND_FRIENDS && _state.value.phoneContactsInApp.isEmpty()) {
            loadPhoneContacts()
        }
    }
    
    private fun dismissInviteDialog() {
        _state.update { it.copy(showInviteDialog = false, inviteUser = null) }
    }
    
    private fun confirmAddInviteUser() {
        val user = _state.value.inviteUser ?: return
        addContact(user.userId, mutual = true)
        dismissInviteDialog()
    }
    
    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
