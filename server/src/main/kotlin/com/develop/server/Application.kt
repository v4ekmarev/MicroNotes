package com.develop.server

import com.develop.server.di.serverModule
import com.develop.server.plugins.configureAuthentication
import com.develop.server.plugins.configureCORS
import com.develop.server.plugins.configureRouting
import com.develop.server.plugins.configureSerialization
import com.develop.server.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        modules(serverModule)
    }
    
    DatabaseFactory.init(environment.config)
    
    configureSerialization()
    configureCORS()
    configureAuthentication()
    configureRouting()
}
