package com.develop.feature.note.route

import com.develop.core.navigation.Route
import kotlinx.serialization.Serializable

/**
 * Маршрут к экрану редактирования/создания заметки.
 * @param id ID заметки, null для создания новой.
 */
@Serializable
data class NoteRoute(val id: Long? = null) : Route
