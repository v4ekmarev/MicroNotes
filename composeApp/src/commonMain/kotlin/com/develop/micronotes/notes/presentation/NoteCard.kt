package com.develop.micronotes.notes.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.develop.micronotes.notes.presentation.model.Note

@Composable
fun NoteCard(
    note: Note,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPinClick: () -> Unit
) {
    val bg = Color((note.color and 0xFFFFFFFFL).toInt())

    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(Modifier.padding(12.dp)) {
            if (note.title.isNotEmpty()) {
                Text(note.title, style = MaterialTheme.typography.titleMedium)
            }
            if (note.content.isNotEmpty()) {
                Text(
                    note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            IconButton(onClick = onPinClick) {
                Icon(Icons.Default.PushPin, contentDescription = null)
            }
            if (selected) {
                Column(
                    Modifier
                        .padding(top = 4.dp)
                        .background(Color.Black.copy(alpha = 0.1f))
                ) {}
            }
        }
    }
}
