package com.develop.feature.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.develop.feature.contacts.route.ContactsRoute
import com.develop.feature.contacts.presentation.contract.ContactsAction
import com.develop.feature.contacts.presentation.contract.ContactsEffect
import com.develop.feature.contacts.presentation.contract.ContactsState
import com.develop.feature.contacts.presentation.contract.ContactsTab
import com.develop.feature.contacts.domain.model.AppContact
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    route: ContactsRoute,
    onBack: () -> Unit = {},
    onShareLink: (String) -> Unit = {}
) {
    val factory = rememberPermissionsControllerFactory()
    val permissionsController = remember(factory) { factory.createPermissionsController() }
    
    val viewModel: ContactsViewModel = koinViewModel(
        parameters = { parametersOf(route, permissionsController) }
    )
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    BindEffect(permissionsController)
    
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ContactsEffect.ShareLink -> onShareLink(effect.link)
                ContactsEffect.ContactAdded -> snackbarHostState.showSnackbar("Контакт добавлен")
                ContactsEffect.ContactRemoved -> snackbarHostState.showSnackbar("Контакт удалён")
            }
        }
    }
    
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onAction(ContactsAction.DismissError)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Контакты") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onAction(ContactsAction.ShareInviteLink) }) {
                        Icon(Icons.Default.Share, contentDescription = "Пригласить")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.selectedTab == ContactsTab.MY_CONTACTS) {
                FloatingActionButton(
                    onClick = { viewModel.onAction(ContactsAction.SelectTab(ContactsTab.FIND_FRIENDS)) }
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Найти друзей")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SecondaryTabRow(
                selectedTabIndex = state.selectedTab.ordinal
            ) {
                Tab(
                    selected = state.selectedTab == ContactsTab.MY_CONTACTS,
                    onClick = { viewModel.onAction(ContactsAction.SelectTab(ContactsTab.MY_CONTACTS)) },
                    text = { Text("Мои контакты") }
                )
                Tab(
                    selected = state.selectedTab == ContactsTab.FIND_FRIENDS,
                    onClick = { viewModel.onAction(ContactsAction.SelectTab(ContactsTab.FIND_FRIENDS)) },
                    text = { Text("Найти друзей") }
                )
            }
            
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.selectedTab == ContactsTab.MY_CONTACTS -> {
                    MyContactsList(
                        contacts = state.appContacts,
                        onRemove = { viewModel.onAction(ContactsAction.RemoveContact(it)) }
                    )
                }
                state.selectedTab == ContactsTab.FIND_FRIENDS -> {
                    FindFriendsList(
                        contacts = state.phoneContactsInApp,
                        existingContacts = state.appContacts,
                        onAdd = { viewModel.onAction(ContactsAction.AddContact(it)) },
                        onShareInvite = { viewModel.onAction(ContactsAction.ShareInviteLink) }
                    )
                }
            }
        }
    }
    
    if (state.showInviteDialog && state.inviteUser != null) {
        InviteUserDialog(
            user = state.inviteUser!!,
            onConfirm = { viewModel.onAction(ContactsAction.ConfirmAddInviteUser) },
            onDismiss = { viewModel.onAction(ContactsAction.DismissInviteDialog) }
        )
    }
}

@Composable
private fun MyContactsList(
    contacts: List<AppContact>,
    onRemove: (Long) -> Unit
) {
    if (contacts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Нет контактов",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Пригласите друзей или найдите их в телефонной книге",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts, key = { it.id }) { contact ->
                ContactCard(
                    contact = contact,
                    trailing = {
                        IconButton(onClick = { onRemove(contact.id) }) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Удалить",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FindFriendsList(
    contacts: List<AppContact>,
    existingContacts: List<AppContact>,
    onAdd: (Long) -> Unit,
    onShareInvite: () -> Unit
) {
    val existingUserIds = existingContacts.map { it.userId }.toSet()
    val newContacts = contacts.filter { it.userId !in existingUserIds }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (newContacts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Никого не нашли в телефонной книге",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onShareInvite) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Пригласить друзей")
                        }
                    }
                }
            }
        } else {
            items(newContacts, key = { it.userId }) { contact ->
                ContactCard(
                    contact = contact,
                    trailing = {
                        IconButton(onClick = { onAdd(contact.userId) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Добавить",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ContactCard(
    contact: AppContact,
    trailing: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (contact.displayName ?: contact.username ?: contact.phone ?: "?")
                        .take(1)
                        .uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.displayName ?: contact.username ?: "Без имени",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                contact.phone?.let { phone ->
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            trailing()
        }
    }
}

@Composable
private fun InviteUserDialog(
    user: AppContact,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить в контакты?") },
        text = {
            Text("Добавить ${user.username ?: user.phone ?: "пользователя"} в ваши контакты?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
