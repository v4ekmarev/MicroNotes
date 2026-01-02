package com.develop.server.plugins

import com.develop.server.routes.authRoutes
import com.develop.server.routes.contactRoutes
import com.develop.server.routes.inboxRoutes
import com.develop.server.routes.profileRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respondText("OK")
        }
        
        authRoutes()
        
        authenticate("auth-jwt") {
            contactRoutes()
            inboxRoutes()
            profileRoutes()
        }
    }
}
