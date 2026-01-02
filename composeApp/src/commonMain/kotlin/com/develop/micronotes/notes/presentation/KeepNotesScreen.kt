package com.develop.micronotes.notes.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.develop.micronotes.notes.KeepNotesViewModel
import com.develop.micronotes.notes.presentation.data.NotesState
import com.develop.micronotes.notes.presentation.model.Note
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun KeepNotesScreen(
    vm: KeepNotesViewModel = koinViewModel<KeepNotesViewModel>(),
    onOpenNote: (Note) -> Unit = {}
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            NotesTopBar(
                query = state.query,
                selectionMode = state.selectionMode,
                selectedCount = state.selection.size,
                isGrid = state.isGrid,
                onQueryChange = vm::onQueryChange,
                onToggleGrid = vm::toggleGrid,
                onDeleteSelected = vm::deleteSelected
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onOpenNote(
                    Note(
                        id = "new-${'$'}{System.currentTimeMillis()}",
                        title = "",
                        content = "",
                        color = 0xFFFFFFFF
                    )
                )
            }) { Icon(Icons.Default.Add, contentDescription = null) }
        }
    ) { padding ->
        NotesContent(
            state = state,
            padding = padding,
            onCardClick = { note ->
                if (state.selectionMode) vm.toggleSelect(note.id) else onOpenNote(note)
            },
            onCardLongClick = { vm.toggleSelect(it.id) },
            onPinClick = { vm.togglePin(it) }
        )
    }
}

@Composable
private fun NotesContent(
    state: NotesState,
    padding: PaddingValues,
    onCardClick: (Note) -> Unit,
    onCardLongClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        if (state.notesPinned.isNotEmpty()) {
            SectionGrid(
                title = "Pinned",
                notes = state.notesPinned,
                columns = state.gridColumns,
                onCardClick = onCardClick,
                onCardLongClick = onCardLongClick,
                onPinClick = onPinClick
            )
        }
        SectionGrid(
            title = if (state.notesPinned.isNotEmpty()) "Others" else "",
            notes = state.notesOthers,
            columns = state.gridColumns,
            onCardClick = onCardClick,
            onCardLongClick = onCardLongClick,
            onPinClick = onPinClick
        )
    }
}

@Composable
private fun SectionGrid(
    title: String,
    notes: List<Note>,
    columns: Int,
    onCardClick: (Note) -> Unit,
    onCardLongClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
) {
    if (title.isNotEmpty()) {
        Text(title)
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                selected = false,
                onClick = { onCardClick(note) },
                onLongClick = { onCardLongClick(note) },
                onPinClick = { onPinClick(note) }
            )
        }
    }
}

@Preview
@Composable
private fun KeepNotesScreenPreview() {
    KeepNotesScreen(
        onOpenNote = {}
    )
}
