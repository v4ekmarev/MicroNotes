package com.develop.server.repository

import com.develop.server.database.tables.PendingShares
import com.develop.server.database.tables.Users
import com.develop.server.models.PendingShare
import com.develop.server.models.SendNoteResponse
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock

/**
 * Репозиторий для транзитного шаринга заметок.
 * 
 * Заметки хранятся на сервере временно, пока получатель не подтвердит получение.
 */
class PendingShareRepository {
    
    /**
     * Отправить заметку пользователю.
     */
    fun sendNote(senderId: Long, recipientId: Long, title: String, content: String): SendNoteResponse? {
        return transaction {
            // Проверяем, что получатель существует
            val recipient = Users.selectAll().where { Users.id eq recipientId }.singleOrNull()
                ?: return@transaction null
            
            val now = Clock.System.now()
            val id = PendingShares.insertAndGetId {
                it[PendingShares.senderId] = senderId
                it[PendingShares.recipientId] = recipientId
                it[PendingShares.title] = title
                it[PendingShares.content] = content
                it[createdAt] = now
            }
            
            SendNoteResponse(
                id = id.value,
                recipientId = recipientId,
                recipientUsername = recipient[Users.username]
            )
        }
    }
    
    /**
     * Отправить заметку нескольким пользователям.
     */
    fun sendNoteToMany(senderId: Long, recipientIds: List<Long>, title: String, content: String): List<SendNoteResponse> {
        return transaction {
            val now = Clock.System.now()
            val results = mutableListOf<SendNoteResponse>()
            
            for (recipientId in recipientIds) {
                val recipient = Users.selectAll().where { Users.id eq recipientId }.singleOrNull()
                    ?: continue
                
                val id = PendingShares.insertAndGetId {
                    it[PendingShares.senderId] = senderId
                    it[PendingShares.recipientId] = recipientId
                    it[PendingShares.title] = title
                    it[PendingShares.content] = content
                    it[createdAt] = now
                }
                
                results.add(SendNoteResponse(
                    id = id.value,
                    recipientId = recipientId,
                    recipientUsername = recipient[Users.username]
                ))
            }
            
            results
        }
    }
    
    /**
     * Получить входящие заметки для пользователя (inbox).
     */
    fun getInbox(userId: Long): List<PendingShare> {
        return transaction {
            (PendingShares innerJoin Users)
                .selectAll()
                .where { PendingShares.recipientId eq userId }
                .map { row ->
                    PendingShare(
                        id = row[PendingShares.id].value,
                        senderId = row[PendingShares.senderId].value,
                        senderUsername = row[Users.username],
                        senderPhone = row[Users.phone],
                        title = row[PendingShares.title],
                        content = row[PendingShares.content],
                        createdAt = row[PendingShares.createdAt]
                    )
                }
        }
    }
    
    /**
     * Получить конкретную входящую заметку.
     */
    fun getInboxItem(userId: Long, shareId: Long): PendingShare? {
        return transaction {
            (PendingShares innerJoin Users)
                .selectAll()
                .where { (PendingShares.id eq shareId) and (PendingShares.recipientId eq userId) }
                .singleOrNull()
                ?.let { row ->
                    PendingShare(
                        id = row[PendingShares.id].value,
                        senderId = row[PendingShares.senderId].value,
                        senderUsername = row[Users.username],
                        senderPhone = row[Users.phone],
                        title = row[PendingShares.title],
                        content = row[PendingShares.content],
                        createdAt = row[PendingShares.createdAt]
                    )
                }
        }
    }
    
    /**
     * Подтвердить получение заметки и удалить её с сервера.
     */
    fun acknowledgeAndDelete(userId: Long, shareId: Long): Boolean {
        return transaction {
            PendingShares.deleteWhere { 
                (id eq shareId) and (recipientId eq userId) 
            } > 0
        }
    }
    
    /**
     * Получить количество непрочитанных заметок.
     */
    fun getInboxCount(userId: Long): Long {
        return transaction {
            PendingShares.selectAll()
                .where { PendingShares.recipientId eq userId }
                .count()
        }
    }
    
    /**
     * Отменить отправку (только отправитель может отменить).
     */
    fun cancelSend(senderId: Long, shareId: Long): Boolean {
        return transaction {
            PendingShares.deleteWhere { 
                (id eq shareId) and (PendingShares.senderId eq senderId) 
            } > 0
        }
    }
}
