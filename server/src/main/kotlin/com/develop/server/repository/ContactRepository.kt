package com.develop.server.repository

import com.develop.server.database.tables.Contacts
import com.develop.server.database.tables.Users
import com.develop.server.models.Contact
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock

class ContactRepository {
    
    fun addContact(userId: Long, contactUserId: Long, mutual: Boolean): Contact? {
        return transaction {
            // Проверяем, что контакт ещё не добавлен
            val existing = Contacts.selectAll()
                .where { (Contacts.userId eq userId) and (Contacts.contactUserId eq contactUserId) }
                .singleOrNull()
            
            if (existing != null && !mutual) return@transaction null
            
            val now = Clock.System.now()
            val id = Contacts.insertAndGetId {
                it[Contacts.userId] = userId
                it[Contacts.contactUserId] = contactUserId
                it[addedAt] = now
            }
            
            // Взаимное добавление: добавляем обратную связь
            if (mutual) {
                val reverseExists = Contacts.selectAll()
                    .where { (Contacts.userId eq contactUserId) and (Contacts.contactUserId eq userId) }
                    .singleOrNull()
                
                if (reverseExists == null) {
                    Contacts.insertAndGetId {
                        it[Contacts.userId] = contactUserId
                        it[Contacts.contactUserId] = userId
                        it[addedAt] = now
                    }
                }
            }
            
            // Получаем данные контакта
            val contactUser = Users.selectAll().where { Users.id eq contactUserId }.single()
            
            Contact(
                id = id.value,
                userId = contactUserId,
                username = contactUser[Users.username],
                phone = contactUser[Users.phone],
                addedAt = now
            )
        }
    }
    
    fun getContacts(userId: Long): List<Contact> {
        return transaction {
            Contacts.innerJoin(Users, { contactUserId }, { Users.id })
                .selectAll()
                .where { Contacts.userId eq userId }
                .map { row ->
                    Contact(
                        id = row[Contacts.id].value,
                        userId = row[Users.id].value,
                        username = row[Users.username],
                        phone = row[Users.phone],
                        addedAt = row[Contacts.addedAt]
                    )
                }
        }
    }
    
    fun removeContact(userId: Long, contactId: Long): Boolean {
        return transaction {
            Contacts.deleteWhere { 
                (Contacts.userId eq userId) and (Contacts.id eq contactId) 
            } > 0
        }
    }
}
