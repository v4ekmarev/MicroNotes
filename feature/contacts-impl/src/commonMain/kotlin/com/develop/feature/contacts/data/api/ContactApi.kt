package com.develop.feature.contacts.data.api

import com.develop.feature.contacts.data.models.AddContactRequest
import com.develop.feature.contacts.data.models.ContactResponse
import com.develop.feature.contacts.data.models.FindUsersByPhonesRequest
import com.develop.feature.contacts.data.models.FoundUserResponse
import com.develop.feature.contacts.data.models.InviteLinkResponse
import com.develop.feature.contacts.data.models.UserProfileResponse
import com.develop.feature.contacts.data.models.UserSearchResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ContactApi(private val client: HttpClient) {
    
    suspend fun getContacts(): Result<List<ContactResponse>> {
        return runCatching {
            client.get("/api/contacts").body()
        }
    }
    
    suspend fun addContact(userId: Long, mutual: Boolean): Result<ContactResponse> {
        return runCatching {
            client.post("/api/contacts") {
                contentType(ContentType.Application.Json)
                setBody(AddContactRequest(userId, mutual))
            }.body()
        }
    }
    
    suspend fun removeContact(contactId: Long): Result<Unit> {
        return runCatching {
            client.delete("/api/contacts/$contactId")
        }
    }
    
    suspend fun findUsersByPhones(phones: List<String>): Result<List<FoundUserResponse>> {
        return runCatching {
            client.post("/api/users/find-by-phones") {
                contentType(ContentType.Application.Json)
                setBody(FindUsersByPhonesRequest(phones))
            }.body()
        }
    }
    
    suspend fun getInviteLink(): Result<InviteLinkResponse> {
        return runCatching {
            client.get("/api/users/me/invite-link").body()
        }
    }
    
    suspend fun getUserById(userId: Long): Result<UserProfileResponse> {
        return runCatching {
            client.get("/api/users/$userId").body()
        }
    }
    
    suspend fun searchUsers(query: String): Result<List<UserSearchResponse>> {
        return runCatching {
            client.get("/api/contacts/search") {
                parameter("query", query)
            }.body()
        }
    }
}
