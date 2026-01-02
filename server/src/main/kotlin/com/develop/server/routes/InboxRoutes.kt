package com.develop.server.routes

import com.develop.server.models.SendNoteRequest
import com.develop.server.models.SendNoteToManyRequest
import com.develop.server.plugins.userId
import com.develop.server.repository.PendingShareRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class InboxCountResponse(val count: Long)

fun Route.inboxRoutes() {
    val shareRepository by inject<PendingShareRepository>()
    
    route("/api/inbox") {
        
        /**
         * Получить входящие заметки.
         */
        get {
            val userId = call.userId()
            val inbox = shareRepository.getInbox(userId)
            call.respond(inbox)
        }
        
        /**
         * Получить количество входящих заметок.
         */
        get("/count") {
            val userId = call.userId()
            val count = shareRepository.getInboxCount(userId)
            call.respond(InboxCountResponse(count))
        }
        
        /**
         * Получить конкретную входящую заметку.
         */
        get("/{id}") {
            val userId = call.userId()
            val shareId = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val item = shareRepository.getInboxItem(userId, shareId)
            if (item == null) {
                call.respond(HttpStatusCode.NotFound, "Not found")
            } else {
                call.respond(item)
            }
        }
        
        /**
         * Подтвердить получение и удалить заметку с сервера.
         * Клиент вызывает после сохранения заметки локально.
         */
        post("/{id}/ack") {
            val userId = call.userId()
            val shareId = call.parameters["id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val deleted = shareRepository.acknowledgeAndDelete(userId, shareId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Not found")
            }
        }
    }
    
    route("/api/send") {
        
        /**
         * Отправить заметку одному пользователю.
         */
        post {
            val userId = call.userId()
            val request = call.receive<SendNoteRequest>()
            
            val result = shareRepository.sendNote(
                senderId = userId,
                recipientId = request.recipientId,
                title = request.title,
                content = request.content
            )
            
            if (result == null) {
                call.respond(HttpStatusCode.NotFound, "Recipient not found")
            } else {
                call.respond(HttpStatusCode.Created, result)
            }
        }
        
        /**
         * Отправить заметку нескольким пользователям.
         */
        post("/many") {
            val userId = call.userId()
            val request = call.receive<SendNoteToManyRequest>()
            
            if (request.recipientIds.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "No recipients specified")
                return@post
            }
            
            val results = shareRepository.sendNoteToMany(
                senderId = userId,
                recipientIds = request.recipientIds,
                title = request.title,
                content = request.content
            )
            
            call.respond(HttpStatusCode.Created, results)
        }
        
        /**
         * Отменить отправку (пока получатель не забрал).
         */
        delete("/{id}") {
            val userId = call.userId()
            val shareId = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val deleted = shareRepository.cancelSend(userId, shareId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Not found or already received")
            }
        }
    }
}
