package com.develop.feature.note.data.api

import com.develop.feature.note.data.models.InboxCountResponse
import com.develop.feature.note.data.models.PendingShareResponse
import com.develop.feature.note.data.models.SendNoteRequest
import com.develop.feature.note.data.models.SendNoteResponse
import com.develop.feature.note.data.models.SendNoteToManyRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class NoteSharingApi(private val client: HttpClient) {
    
    suspend fun getInbox(): Result<List<PendingShareResponse>> {
        return runCatching {
            client.get("/api/inbox").body()
        }
    }
    
    suspend fun getInboxCount(): Result<InboxCountResponse> {
        return runCatching {
            client.get("/api/inbox/count").body()
        }
    }
    
    suspend fun getInboxItem(shareId: Long): Result<PendingShareResponse> {
        return runCatching {
            client.get("/api/inbox/$shareId").body()
        }
    }
    
    suspend fun acknowledgeReceived(shareId: Long): Result<Unit> {
        return runCatching {
            client.post("/api/inbox/$shareId/ack")
        }
    }
    
    suspend fun sendNote(recipientId: Long, title: String, content: String): Result<SendNoteResponse> {
        return runCatching {
            client.post("/api/send") {
                contentType(ContentType.Application.Json)
                setBody(SendNoteRequest(recipientId, title, content))
            }.body()
        }
    }
    
    suspend fun sendNoteToMany(
        recipientIds: List<Long>,
        title: String,
        content: String
    ): Result<List<SendNoteResponse>> {
        return runCatching {
            client.post("/api/send/many") {
                contentType(ContentType.Application.Json)
                setBody(SendNoteToManyRequest(recipientIds, title, content))
            }.body()
        }
    }
    
    suspend fun cancelSend(shareId: Long): Result<Unit> {
        return runCatching {
            client.delete("/api/send/$shareId")
        }
    }
}
