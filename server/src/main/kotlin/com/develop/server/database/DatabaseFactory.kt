package com.develop.server.database

import com.develop.server.database.tables.Users
import com.develop.server.database.tables.PendingShares
import com.develop.server.database.tables.Contacts
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    
    fun init(config: ApplicationConfig) {
        val driverClassName = config.property("database.driverClassName").getString()
        val jdbcUrl = config.property("database.jdbcUrl").getString()
        
        Database.connect(
            url = jdbcUrl,
            driver = driverClassName
        )
        
        transaction {
            SchemaUtils.create(
                Users,
                PendingShares,
                Contacts
            )
        }
    }
}
