package com.develop.server.routes

import com.develop.server.models.DeviceAuthRequest
import com.develop.server.models.DeviceAuthResponse
import com.develop.server.plugins.JwtConfig
import com.develop.server.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val userRepository by inject<UserRepository>()
    
    route("/api/auth") {
        post("/device") {
            val request = call.receive<DeviceAuthRequest>()
            
            val (user, isNewUser) = userRepository.findOrCreateByDeviceId(request.deviceId)
            
            val token = JwtConfig.makeToken(
                config = call.application.environment.config,
                userId = user.id,
                deviceId = user.deviceId
            )
            
            val statusCode = if (isNewUser) HttpStatusCode.Created else HttpStatusCode.OK
            call.respond(statusCode, DeviceAuthResponse(
                token = token,
                deviceId = user.deviceId,
                isNewUser = isNewUser
            ))
        }
    }
}
