package com.develop.micronotes.notes.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    query: String,
    selectionMode: Boolean,
    selectedCount: Int,
    isGrid: Boolean,
    onQueryChange: (String) -> Unit,
    onToggleGrid: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    TopAppBar(
        title = {
            if (selectionMode) {
                Text("${'$'}selectedCount selected")
            } else {
                SearchBarField(query = query, onQueryChange = onQueryChange)
            }
        },
        actions = {
            if (selectionMode) {
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            } else {
                IconButton(onClick = onToggleGrid) {
                    Icon(if (isGrid) Icons.Default.List else Icons.Default.GridView, contentDescription = null)
                }
            }
        }
    )
}

@Composable
private fun SearchBarField(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
    )
}
