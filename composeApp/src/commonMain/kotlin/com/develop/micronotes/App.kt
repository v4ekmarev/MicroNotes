package com.develop.micronotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.develop.core.common.Context
import com.develop.core.navigation.Route
import com.develop.feature.contacts.route.ContactsRoute
import com.develop.feature.contacts.presentation.ContactsScreen
import com.develop.feature.note.route.NoteRoute
import com.develop.feature.note.list.route.NoteListRoute
import com.develop.feature.note.presentation.NoteScreen
import com.develop.feature.note_list.presentation.components.NoteListScreen
import com.develop.feature.profile.route.ProfileRoute
import com.develop.feature.profile.presentation.ProfileScreen
import com.develop.micronotes.di.appModule
import com.develop.feature.splash.route.SplashRoute
import com.develop.feature.splash.presentation.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App(context: Context, inviteUserId: Long? = null) {
    val backStack = remember { mutableStateListOf<Route>(SplashRoute) }
    val pendingInviteUserId = remember { mutableStateOf(inviteUserId) }
    
    KoinApplication(application = {
        modules(appModule(context))
    }) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is SplashRoute -> NavEntry(key) {
                        SplashScreen(
                            onAuthComplete = {
                                backStack.clear()
                                backStack.add(NoteListRoute)
                                pendingInviteUserId.value?.let { userId ->
                                    backStack.add(ContactsRoute(inviteUserId = userId))
                                    pendingInviteUserId.value = null
                                }
                            }
                        )
                    }
                    is NoteListRoute -> NavEntry(key) {
                        NoteListScreen(
                            onOpenNote = { id -> backStack.add(NoteRoute(id)) },
                            onOpenContacts = { backStack.add(ContactsRoute()) },
                            onOpenProfile = { backStack.add(ProfileRoute) }
                        )
                    }
                    is NoteRoute -> NavEntry(key) {
                        NoteScreen(
                            route = key,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }
                    is ContactsRoute -> NavEntry(key) {
                        ContactsScreen(
                            route = key,
                            onBack = { backStack.removeLastOrNull() },
                            onShareLink = { link -> shareText(context, link) }
                        )
                    }
                    is ProfileRoute -> NavEntry(key) {
                        ProfileScreen(
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }
                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}

expect fun shareText(context: Context, text: String)