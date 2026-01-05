package com.develop.feature.contacts.data

import com.develop.core.common.AppLogger
import com.develop.feature.contacts.domain.PhoneContactsProvider
import com.develop.feature.contacts.domain.model.PhoneContact
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Contacts.CNContactFetchRequest
import platform.Contacts.CNContactGivenNameKey
import platform.Contacts.CNContactFamilyNameKey
import platform.Contacts.CNContactPhoneNumbersKey
import platform.Contacts.CNContactStore
import platform.Contacts.CNLabeledValue
import platform.Contacts.CNPhoneNumber

@OptIn(ExperimentalForeignApi::class)
class IosPhoneContactsProvider : PhoneContactsProvider {
    
    override suspend fun getPhoneContacts(): List<PhoneContact> {
        val contacts = mutableListOf<PhoneContact>()
        val store = CNContactStore()
        
        val keysToFetch = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey
        )
        
        val request = CNContactFetchRequest(keysToFetch = keysToFetch)
        
        try {
            store.enumerateContactsWithFetchRequest(request, error = null) { contact, _ ->
                val name = "${contact?.givenName ?: ""} ${contact?.familyName ?: ""}".trim()
                
                @Suppress("UNCHECKED_CAST")
                val phoneNumbers = contact?.phoneNumbers as? List<CNLabeledValue> ?: emptyList()
                
                phoneNumbers.forEach { labeledValue ->
                    val phoneNumber = labeledValue.value as? CNPhoneNumber
                    phoneNumber?.stringValue?.let { phone ->
                        if (name.isNotEmpty() && phone.isNotEmpty()) {
                            contacts.add(PhoneContact(name = name, phone = phone))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            AppLogger.d("IosPhoneContactsProvider", e.message.orEmpty())
        }
        
        return contacts.distinctBy { normalizePhone(it.phone) }
    }
    
    private fun normalizePhone(phone: String): String {
        return phone.filter { it.isDigit() || it == '+' }
    }
}
