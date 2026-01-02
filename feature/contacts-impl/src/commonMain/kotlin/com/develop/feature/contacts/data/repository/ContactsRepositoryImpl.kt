package com.develop.feature.contacts.data.repository

import com.develop.data.database.dao.ContactDao
import com.develop.data.database.entity.ContactEntity
import com.develop.feature.contacts.data.api.ContactApi
import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.model.AppContact
import com.develop.feature.contacts.domain.model.PhoneContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ContactsRepositoryImpl(
    private val contactApi: ContactApi,
    private val contactDao: ContactDao,
) : ContactsRepository {
    
    @OptIn(ExperimentalTime::class)
    override suspend fun getAppContacts(): Result<List<AppContact>> {
        return contactApi.getContacts().map { contacts ->
            val now = Clock.System.now().toEpochMilliseconds()
            val entities = contacts.map { dto ->
                ContactEntity(
                    id = 0L,
                    userId = dto.userId,
                    username = dto.username,
                    phone = dto.phone,
                    syncedAt = now
                )
            }
            contactDao.upsertAll(entities)
            
            contacts.map { dto ->
                AppContact(
                    id = dto.id,
                    userId = dto.userId,
                    username = dto.username,
                    phone = dto.phone
                )
            }
        }
    }
    
    override suspend fun getCachedContacts(): List<AppContact> {
        return contactDao.getAll().map { entity ->
            AppContact(
                id = entity.id,
                userId = entity.userId,
                username = entity.username,
                phone = entity.phone,
                displayName = entity.displayName
            )
        }
    }
    
    override fun observeCachedContacts(): Flow<List<AppContact>> {
        return contactDao.observeAll().map { entities ->
            entities.map { entity ->
                AppContact(
                    id = entity.id,
                    userId = entity.userId,
                    username = entity.username,
                    phone = entity.phone,
                    displayName = entity.displayName
                )
            }
        }
    }
    
    @OptIn(ExperimentalTime::class)
    override suspend fun addContact(userId: Long, mutual: Boolean): Result<AppContact> {
        return contactApi.addContact(userId, mutual).map { dto ->
            val now = Clock.System.now().toEpochMilliseconds()
            contactDao.upsert(
                ContactEntity(
                    id = 0L,
                    userId = dto.userId,
                    username = dto.username,
                    phone = dto.phone,
                    syncedAt = now
                )
            )
            AppContact(
                id = dto.id,
                userId = dto.userId,
                username = dto.username,
                phone = dto.phone
            )
        }
    }
    
    override suspend fun removeContact(contactId: Long): Result<Unit> {
        return contactApi.removeContact(contactId).also {
            if (it.isSuccess) {
                contactDao.deleteById(contactId)
            }
        }
    }
    
    override suspend fun findUsersFromPhoneContacts(phoneContacts: List<PhoneContact>): Result<List<AppContact>> {
        val normalizedPhones = phoneContacts.map { normalizePhone(it.phone) }
        val phoneToName = phoneContacts.associate { normalizePhone(it.phone) to it.name }
        
        return contactApi.findUsersByPhones(normalizedPhones).map { users ->
            users.map { dto ->
                AppContact(
                    id = 0,
                    userId = dto.id,
                    username = dto.username,
                    phone = dto.phone,
                    displayName = phoneToName[dto.phone]
                )
            }
        }
    }
    
    override suspend fun getInviteLink(): Result<String> {
        return contactApi.getInviteLink().map { it.inviteLink }
    }
    
    override suspend fun getUserById(userId: Long): Result<AppContact?> {
        return contactApi.getUserById(userId).map { dto ->
            AppContact(
                id = 0,
                userId = dto.id,
                username = dto.username,
                phone = dto.phone
            )
        }
    }
    
    private fun normalizePhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return when {
            digits.startsWith("8") && digits.length == 11 -> "+7${digits.drop(1)}"
            digits.startsWith("7") && digits.length == 11 -> "+$digits"
            !digits.startsWith("+") -> "+$digits"
            else -> digits
        }
    }
}
