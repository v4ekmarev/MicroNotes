package com.develop.feature.note.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.develop.feature.note.presentation.contract.NoteAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopBar(
    onAction: (NoteAction) -> Unit,
) {
    TopAppBar(
        title = { Text(text = "", maxLines = 1, overflow = TextOverflow.Clip) },
        navigationIcon = {
            IconButton(onClick = {
                onAction.invoke(NoteAction.SaveNote)
            }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { onAction.invoke(NoteAction.SelectReminder) }) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Напомнить позже")
            }
            IconButton(onClick = { onAction.invoke(NoteAction.SelectShareContacts) }) {
                Icon(Icons.Outlined.PersonAdd, contentDescription = "Поделиться")
            }
            IconButton(onClick = { /* TODO archive */ }) {
                Icon(Icons.Outlined.Archive, contentDescription = "Archive")
            }
            IconButton(onClick = { /* TODO more */ }) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More")
            }
        }
    )
}
