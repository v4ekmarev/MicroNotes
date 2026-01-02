package com.develop.feature.contacts.domain

import com.develop.feature.contacts.domain.model.PhoneContact

interface PhoneContactsProvider {
    suspend fun getPhoneContacts(): List<PhoneContact>
}
