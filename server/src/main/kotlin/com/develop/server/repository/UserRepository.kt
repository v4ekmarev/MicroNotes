package com.develop.server.repository

import com.develop.server.database.tables.Users
import com.develop.server.models.User
import com.develop.server.models.FoundUser
import com.develop.server.models.UserSearchResult
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.toKotlinInstant

class UserRepository {

    fun findOrCreateByDeviceId(deviceId: String?): Pair<User, Boolean> {
        val now = Instant.now().toKotlinInstant()
        
        return transaction {
            if (deviceId != null) {
                val existingRow = Users.selectAll().where { Users.deviceId eq deviceId }.singleOrNull()
                if (existingRow != null) {
                    val user = User(
                        id = existingRow[Users.id].value,
                        deviceId = existingRow[Users.deviceId],
                        username = existingRow[Users.username],
                        phone = existingRow[Users.phone],
                        createdAt = existingRow[Users.createdAt]
                    )
                    return@transaction user to false
                }
            }
            
            val newDeviceId = deviceId ?: UUID.randomUUID().toString()
            
            val id = Users.insertAndGetId {
                it[Users.deviceId] = newDeviceId
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            val user = User(
                id = id.value,
                deviceId = newDeviceId,
                createdAt = now
            )
            user to true
        }
    }
    
    fun findById(id: Long): User? {
        return transaction {
            Users.selectAll().where { Users.id eq id }.singleOrNull()?.let { row ->
                User(
                    id = row[Users.id].value,
                    deviceId = row[Users.deviceId],
                    username = row[Users.username],
                    phone = row[Users.phone],
                    createdAt = row[Users.createdAt]
                )
            }
        }
    }
    
    fun findByDeviceId(deviceId: String): User? {
        return transaction {
            Users.selectAll().where { Users.deviceId eq deviceId }.singleOrNull()?.let { row ->
                User(
                    id = row[Users.id].value,
                    deviceId = row[Users.deviceId],
                    username = row[Users.username],
                    phone = row[Users.phone],
                    createdAt = row[Users.createdAt]
                )
            }
        }
    }
    
    fun updateProfile(userId: Long, username: String?, phone: String?): User? {
        return transaction {
            val now = Instant.now().toKotlinInstant()
            Users.update({ Users.id eq userId }) {
                if (username != null) it[Users.username] = username
                if (phone != null) it[Users.phone] = phone
                it[updatedAt] = now
            }
            findById(userId)
        }
    }
    
    fun searchByUsernameOrPhone(query: String, excludeUserId: Long): List<UserSearchResult> {
        return transaction {
            Users.selectAll()
                .where { 
                    (Users.username like "%$query%") or (Users.phone like "%$query%") 
                }
                .andWhere { Users.id neq excludeUserId }
                .limit(20)
                .map { row ->
                    UserSearchResult(
                        id = row[Users.id].value,
                        username = row[Users.username],
                        phone = row[Users.phone]
                    )
                }
        }
    }
    
    fun findByPhones(phones: List<String>, excludeUserId: Long): List<FoundUser> {
        if (phones.isEmpty()) return emptyList()
        
        return transaction {
            Users.selectAll()
                .where { Users.phone inList phones }
                .andWhere { Users.id neq excludeUserId }
                .mapNotNull { row ->
                    val phone = row[Users.phone] ?: return@mapNotNull null
                    FoundUser(
                        id = row[Users.id].value,
                        phone = phone,
                        username = row[Users.username]
                    )
                }
        }
    }
}
