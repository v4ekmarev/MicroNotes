package com.develop.server.routes

import com.develop.server.models.UpdateProfileRequest
import com.develop.server.plugins.userId
import com.develop.server.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.profileRoutes() {
    val userRepository by inject<UserRepository>()
    
    route("/api/users/me") {
        get {
            val userId = call.userId()
            val user = userRepository.findById(userId)
            
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }
            
            call.respond(mapOf(
                "id" to user.id,
                "username" to user.username,
                "phone" to user.phone
            ))
        }
        
        put {
            val userId = call.userId()
            val request = call.receive<UpdateProfileRequest>()
            
            val updatedUser = userRepository.updateProfile(
                userId = userId,
                username = request.username,
                phone = request.phone
            )
            
            if (updatedUser == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@put
            }
            
            call.respond(mapOf(
                "id" to updatedUser.id,
                "username" to updatedUser.username,
                "phone" to updatedUser.phone
            ))
        }
    }
}
