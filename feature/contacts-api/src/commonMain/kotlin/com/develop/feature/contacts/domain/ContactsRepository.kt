package com.develop.feature.contacts.domain

import com.develop.feature.contacts.domain.model.AppContact
import com.develop.feature.contacts.domain.model.PhoneContact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun getAppContacts(): Result<List<AppContact>>
    suspend fun getCachedContacts(): List<AppContact>
    fun observeCachedContacts(): Flow<List<AppContact>>
    suspend fun addContact(userId: Long, mutual: Boolean): Result<AppContact>
    suspend fun removeContact(contactId: Long): Result<Unit>
    suspend fun findUsersFromPhoneContacts(phoneContacts: List<PhoneContact>): Result<List<AppContact>>
    suspend fun getInviteLink(): Result<String>
    suspend fun getUserById(userId: Long): Result<AppContact?>
}
