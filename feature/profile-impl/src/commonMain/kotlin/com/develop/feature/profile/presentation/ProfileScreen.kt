package com.develop.feature.profile.presentation

import com.develop.feature.profile.presentation.contract.ProfileAction
import com.develop.feature.profile.presentation.contract.ProfileEffect
import com.develop.feature.profile.presentation.contract.ProfileState

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.ProfileSaved -> snackbarHostState.showSnackbar("Профиль сохранён")
            }
        }
    }
    
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onAction(ProfileAction.DismissError)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = { viewModel.onAction(ProfileAction.CancelEditing) }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Отмена")
                        }
                        IconButton(
                            onClick = { viewModel.onAction(ProfileAction.SaveProfile) },
                            enabled = !state.isSaving
                        ) {
                            Icon(Icons.Outlined.Check, contentDescription = "Сохранить")
                        }
                    } else {
                        IconButton(onClick = { viewModel.onAction(ProfileAction.StartEditing) }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Редактировать")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                ProfileContent(
                    state = state,
                    onAction = viewModel::onAction,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        
        Avatar(
            name = state.profile?.username ?: state.editUsername,
            size = 120
        )
        
        Spacer(Modifier.height(32.dp))
        
        if (state.isEditing) {
            EditProfileFields(
                username = state.editUsername,
                phone = state.editPhone,
                onUsernameChange = { onAction(ProfileAction.UpdateUsername(it)) },
                onPhoneChange = { onAction(ProfileAction.UpdatePhone(it)) },
                isSaving = state.isSaving
            )
        } else {
            ProfileInfo(
                username = state.profile?.username,
                phone = state.profile?.phone
            )
        }
    }
}

@Composable
private fun Avatar(
    name: String?,
    size: Int,
    modifier: Modifier = Modifier
) {
    val initials = getInitials(name)
    val backgroundColor = getAvatarColor(name)
    
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = (size / 3).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileInfo(
    username: String?,
    phone: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileField(
            label = "Имя",
            value = username ?: "Не указано"
        )
        ProfileField(
            label = "Телефон",
            value = phone ?: "Не указан"
        )
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EditProfileFields(
    username: String,
    phone: String,
    onUsernameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    isSaving: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
            singleLine = true
        )
        
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        
        if (isSaving) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun getInitials(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    
    val words = name.trim().split("\\s+".toRegex())
    return when {
        words.size >= 2 -> "${words[0].firstOrNull()?.uppercaseChar() ?: ""}${words[1].firstOrNull()?.uppercaseChar() ?: ""}"
        words.size == 1 && words[0].length >= 2 -> words[0].take(2).uppercase()
        words.size == 1 -> words[0].firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        else -> "?"
    }
}

private fun getAvatarColor(name: String?): Color {
    if (name.isNullOrBlank()) return Color(0xFF9E9E9E)
    
    val colors = listOf(
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF673AB7),
        Color(0xFF3F51B5),
        Color(0xFF2196F3),
        Color(0xFF03A9F4),
        Color(0xFF00BCD4),
        Color(0xFF009688),
        Color(0xFF4CAF50),
        Color(0xFF8BC34A),
        Color(0xFFFF9800),
        Color(0xFFFF5722)
    )
    
    val hash = name.hashCode().let { if (it < 0) -it else it }
    return colors[hash % colors.size]
}
