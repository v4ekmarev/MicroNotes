package com.develop.feature.note.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.develop.feature.note.presentation.contract.DialogType
import com.develop.feature.note.presentation.contract.NoteAction
import com.develop.feature.note.presentation.contract.NoteEffect
import com.develop.feature.note.presentation.contract.NoteState
import com.develop.feature.note.presentation.components.NoteBottomSheets
import com.develop.feature.note.presentation.components.NoteContent
import com.develop.feature.note.presentation.components.NoteTopBar
import com.develop.feature.note.presentation.components.ReminderCustomDialog
import com.develop.feature.note.presentation.components.ReminderPermissionDialog
import kotlinx.coroutines.flow.collectLatest
import com.develop.feature.note.route.NoteRoute
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    route: NoteRoute,
    onBack: () -> Unit = {},
) {
    val factory = rememberPermissionsControllerFactory()
    val permissionsController = remember(factory) { factory.createPermissionsController() }

    val viewModel: NoteViewModel = koinViewModel(
        key = "note_${route.id}",
        parameters = { parametersOf(route, permissionsController) }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    BindEffect(permissionsController)

    NoteScreen(
        state = state,
        onBackClick = onBack,
        onAction = { viewModel.doOnAction(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NoteEffect.Back -> onBack()
                NoteEffect.None -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteScreen(
    state: NoteState,
    onBackClick: () -> Unit,
    onAction: (NoteAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { NoteTopBar(onAction = onAction) }
    ) { padding ->
        NoteContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(padding)
        )

        NoteBottomSheets(
            state = state,
            onAction = onAction,
        )
    }

    if (state.dialog is DialogType.ReminderPermission) {
        ReminderPermissionDialog(
            onConfirm = { onAction.invoke(NoteAction.ReminderPermissionDialogConfirmed) },
            onDismiss = { onAction.invoke(NoteAction.ReminderPermissionDialogDismissed) }
        )
    }

    if (state.dialog is DialogType.ReminderCustom) {
        ReminderCustomDialog(
            state = state,
            onAction = onAction,
        )
    }
}

@Preview
@Composable
private fun NoteScreenPreview() {
    NoteScreen(
        state = NoteState(
            title = "Название",
            content = "Заметка"
        ),
        onBackClick = {},
        onAction = {},
    )
}
