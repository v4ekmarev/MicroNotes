package com.develop.server.routes

import com.develop.server.models.AddContactRequest
import com.develop.server.models.FindUsersByPhonesRequest
import com.develop.server.plugins.userId
import com.develop.server.repository.ContactRepository
import com.develop.server.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.contactRoutes() {
    val contactRepository by inject<ContactRepository>()
    val userRepository by inject<UserRepository>()
    
    route("/api/contacts") {
        get {
            val userId = call.userId()
            val contacts = contactRepository.getContacts(userId)
            call.respond(contacts)
        }
        
        post {
            val userId = call.userId()
            val request = call.receive<AddContactRequest>()
            
            val contactUser = userRepository.findById(request.userId)
            if (contactUser == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@post
            }
            
            if (contactUser.id == userId) {
                call.respond(HttpStatusCode.BadRequest, "Cannot add yourself as contact")
                return@post
            }
            
            val contact = contactRepository.addContact(userId, contactUser.id, request.mutual)
            if (contact == null) {
                call.respond(HttpStatusCode.Conflict, "Contact already exists")
                return@post
            }
            
            call.respond(HttpStatusCode.Created, contact)
        }
        
        delete("/{id}") {
            val userId = call.userId()
            val contactId = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")
            
            val deleted = contactRepository.removeContact(userId, contactId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Contact not found")
            }
        }
    }
    
    get("/api/users/search") {
        val userId = call.userId()
        val query = call.request.queryParameters["q"] ?: ""
        
        if (query.length < 2) {
            call.respond(emptyList<Any>())
            return@get
        }
        
        val users = userRepository.searchByUsernameOrPhone(query, userId)
        call.respond(users)
    }
    
    post("/api/users/find-by-phones") {
        val userId = call.userId()
        val request = call.receive<FindUsersByPhonesRequest>()
        
        if (request.phones.size > 500) {
            call.respond(HttpStatusCode.BadRequest, "Too many phones (max 500)")
            return@post
        }
        
        val users = userRepository.findByPhones(request.phones, userId)
        call.respond(users)
    }
    
    get("/api/users/me/invite-link") {
        val userId = call.userId()
        val inviteLink = "https://micronotes.app/invite/$userId"
        call.respond(mapOf("inviteLink" to inviteLink))
    }
    
    get("/api/users/{id}") {
        val targetUserId = call.parameters["id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
        
        val user = userRepository.findById(targetUserId)
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
}
