package com.develop.feature.contacts.route

import com.develop.core.navigation.Route
import kotlinx.serialization.Serializable

/**
 * Маршрут к экрану контактов.
 * @param inviteUserId ID пользователя из invite-ссылки (диплинка), null если обычный переход.
 */
@Serializable
data class ContactsRoute(val inviteUserId: Long? = null) : Route
