package com.develop.feature.note_list.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.develop.feature.note_list.presentation.NoteListViewModel
import com.develop.feature.note_list.presentation.contract.NoteListAction
import com.develop.feature.note_list.presentation.contract.NoteListCategory
import com.develop.feature.note_list.presentation.contract.NoteListItem
import com.develop.feature.note_list.presentation.contract.NoteListState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NoteListScreen(
    viewModel: NoteListViewModel = koinViewModel<NoteListViewModel>(),
    onOpenNote: (Long?) -> Unit = {},
    onOpenContacts: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Обновляем список при каждом появлении экрана
    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }
    
    NoteListScreen(
        state = state,
        doOnAction = { viewModel.doOnAction(it) },
        onOpenNote = onOpenNote,
        onOpenContacts = onOpenContacts,
        onOpenProfile = onOpenProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteListScreen(state: NoteListState, doOnAction: (NoteListAction) -> Unit, onOpenNote: (Long?) -> Unit, onOpenContacts: () -> Unit, onOpenProfile: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "MicroNotes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp))
                Spacer(Modifier.height(16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Notes, contentDescription = null) },
                    label = { Text("Заметки") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Contacts, contentDescription = null) },
                    label = { Text("Контакты") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenContacts()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профиль") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenProfile()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Заметки") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    onOpenNote(null)
                }) { Icon(Icons.Default.Add, contentDescription = null) }
            }
        ) { padding ->
            NoteListContent(
                state = state,
                padding = padding,
                onQueryChange = { doOnAction(NoteListAction.QueryChange(it)) },
                onCardClick = { note ->
                    if (state.selectionMode) {
                        doOnAction(NoteListAction.ToggleSelect(note.id))
                    } else {
                        onOpenNote(note.id)
                    }
                },
                onCardLongClick = { doOnAction(NoteListAction.ToggleSelect(it.id)) },
                onPinClick = { }
            )
        }
    }
}

@Composable
private fun NoteListContent(
    state: NoteListState,
    padding: PaddingValues,
    onQueryChange: (String) -> Unit,
    onCardClick: (NoteListItem) -> Unit,
    onCardLongClick: (NoteListItem) -> Unit,
    onPinClick: (NoteListItem) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        TextField(
            value = state.query,
            onValueChange = onQueryChange,
            placeholder = { Text("Поиск") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        state.categories.forEach { category ->
            CategoryList(
                category = category,
                onRowClick = onCardClick,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryList(
    category: NoteListCategory,
    onRowClick: (NoteListItem) -> Unit,
) {
    if (category.items.isEmpty()) return
    Text(
        text = category.title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            category.items.forEachIndexed { index, note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Отображаем иконку статуса, если есть
                    if (note.statusIcon != null) {
                        Text(
                            text = note.statusIcon,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (note.reminderText != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = note.reminderText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.MoreHoriz, contentDescription = null)
                    }
                }
                if (index != category.items.lastIndex) {
                    HorizontalDivider()
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

//@Preview
//@Composable
//private fun KeepNotesScreenPreview() {
//    val state =  NotesState(notesOthers = listOf(
//        Note(id = 1, title = "Идея", content = "Проверить KMP ViewModel", color = 0xFFFFF59D),
//        Note(id = 2, title = "Список", content = "Мультиселект, Пин, Цвет", color = 0xFFB2EBF2),
//        Note(id = 3, title = "Заметка", content = "Compose Multiplatform", color = 0xFFFFCCBC),
//    ))
//    NotesScreen(state, {}, {})
//}
