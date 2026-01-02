package com.develop.server.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

object Users : LongIdTable("users") {
    val deviceId = varchar("device_id", 36).uniqueIndex()
    val username = varchar("username", 100).nullable()
    val phone = varchar("phone", 20).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
