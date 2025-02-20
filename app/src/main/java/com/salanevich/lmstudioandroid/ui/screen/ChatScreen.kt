package com.salanevich.lmstudioandroid.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salanevich.data.network.body.Role
import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.LmModel
import com.salanevich.domain.model.Message
import com.salanevich.lmstudioandroid.R
import com.salanevich.lmstudioandroid.ui.component.InputFieldWithButton
import com.salanevich.lmstudioandroid.ui.component.ModelSelection
import com.salanevich.lmstudioandroid.ui.theme.LmStudioAndroidTheme
import com.salanevich.lmstudioandroid.vm.ChatIntent
import com.salanevich.lmstudioandroid.vm.ChatSideEffect
import com.salanevich.lmstudioandroid.vm.ChatState
import com.salanevich.lmstudioandroid.vm.ChatViewModel
import com.yazantarifi.compose.library.MarkdownConfig
import com.yazantarifi.compose.library.MarkdownViewComposable
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChatScreen(modifier: Modifier = Modifier, navigateToPreferences: () -> Unit) {
    val vm = hiltViewModel<ChatViewModel>()
    val context = LocalContext.current
    vm.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ChatSideEffect.ShowErrorMessage -> {
                Toast.makeText(context, sideEffect.error, Toast.LENGTH_LONG).show()
            }
            is ChatSideEffect.GoToPreferences -> {
                navigateToPreferences()
            }
        }
    }
    val state = vm.collectAsState()
    ChatScreen(
        modifier = modifier,
        state = state,
        messageAction = { vm.reduce(ChatIntent.GetResponse(it)) },
        saveUrlAction = { vm.reduce(ChatIntent.PutBaseUrl(it)) },
        selectionModelAction = { vm.reduce(ChatIntent.SelectModel(it))},
        reloadModelsAction = { vm.reduce(ChatIntent.LoadModels) },
        goToPreferencesAction = { vm.reduce(ChatIntent.GoToPreferences) },
        requestModelSelectionAction = { vm.reduce(ChatIntent.RequestModelSelection) },
        clearChatAction = { vm.reduce(ChatIntent.ClearChat) }
    )
}

@Composable
private fun ChatScreen(
    modifier: Modifier = Modifier,
    state: State<ChatState>,
    messageAction: (String) -> Unit,
    saveUrlAction: (String) -> Unit,
    selectionModelAction: (String) -> Unit,
    reloadModelsAction: () -> Unit,
    goToPreferencesAction: () -> Unit,
    requestModelSelectionAction: () -> Unit,
    clearChatAction: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                state = state,
                goToPreferencesAction = goToPreferencesAction,
                clearChatAction = clearChatAction
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.value.loading) {
                CircularProgressIndicator()
            } else if (state.value.requestUrl || state.value.fatalError != null) {
                val url = state.value.fatalError?.url
                val error = state.value.fatalError?.message
                InputFieldWithButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    buttonText = stringResource(R.string.save),
                    value = url ?: "",
                    placeholder = stringResource(R.string.http_192_168_0_105_1234),
                    errorMessage = error,
                    action = saveUrlAction
                )
            } else if (state.value.models.size > 1 && state.value.selectedModel == null) {
                ModelSelection(models = state.value.models.map { it.name }, applyButton = {
                    Button(onClick = { selectionModelAction(it) }) {
                        Text(text = stringResource(R.string.apply))
                    }
                })
            } else if (state.value.models.isEmpty()) {
                ShowWarningWithAction(action = reloadModelsAction)
            } else {
                ShowChat(
                    state = state,
                    messageAction = messageAction,
                    requestModelSelectionAction = requestModelSelectionAction,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: State<ChatState>,
    goToPreferencesAction: () -> Unit,
    clearChatAction: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = stringResource(R.string.chat)) },
        actions = {
            if (state.value.chat != null) {
                Button(onClick = clearChatAction) {
                    Text(text = stringResource(R.string.clear))
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            IconButton(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
                onClick = goToPreferencesAction
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
    )
}

@Composable
private fun ShowWarningWithAction(action: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.no_model_was_loaded))
        Button(onClick = action) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
private fun ShowChat(
    modifier: Modifier = Modifier,
    state: State<ChatState>,
    messageAction: (String) -> Unit,
    requestModelSelectionAction: () -> Unit,
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp)) {
        val messages = state.value.chat?.messages ?: emptyList()
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            state = listState
        ) {
            items(messages.size, key = { messages[it].message }) { i ->
                val message = messages[i]
                if (message.role != Role.SYSTEM.value) {
                    ChatItem(message = message.message, role = message.role, model = message.model)
                }
            }
            if (state.value.loadingOfMessage) {
                item {
                    ChatItem(
                        message = "Loading...",
                        role = Role.ASSISTANT.name,
                        model = state.value.selectedModel!!
                    )
                }
            }
        }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                coroutineScope.launch {
                    listState.animateScrollToItem(messages.lastIndex)
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        InputFieldWithButton(
            buttonText = stringResource(R.string.submit),
            reset = true,
            action = messageAction
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (state.value.models.size > 1) {
                        requestModelSelectionAction()
                    }
                },
            text = stringResource(R.string.model, state.value.selectedModel ?: ""),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ChatItem(
    modifier: Modifier = Modifier,
    message: String,
    role: String,
    model: String
) {
    Card(
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = if (Role.USER.value == role) Alignment.End else Alignment.Start
        ) {
            MarkdownViewComposable(
                modifier = Modifier,
                content = message,
                config = MarkdownConfig(
                    isLinksClickable = false,
                    isImagesClickable = false,
                    isScrollEnabled = false,
                    colors = HashMap<String, Color>().apply {
                        this[MarkdownConfig.LINKS_COLOR] = Color.Blue
                        this[MarkdownConfig.TEXT_COLOR] = if (isSystemInDarkTheme()) Color.White else Color.Black
                        this[MarkdownConfig.HASH_TEXT_COLOR] = Color.White
                        this[MarkdownConfig.CODE_BACKGROUND_COLOR] = Color.Gray
                        this[MarkdownConfig.CODE_BLOCK_TEXT_COLOR] = Color.White
                    }
                )
            )
            if (role == Role.ASSISTANT.value) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.role_model, role, model),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
    Spacer(modifier = Modifier.padding(2.dp))
}

@Preview
@Composable
private fun ChatItemUserPreview() {
    LmStudioAndroidTheme {
        ChatItem(
            message = "Message1",
            role = Role.USER.value,
            model = "model1"
        )
    }
}

@Preview
@Composable
private fun ChatItemAssistantPreview() {
    LmStudioAndroidTheme {
        ChatItem(
            message = "Message1",
            role = Role.ASSISTANT.value,
            model = "model1"
        )
    }
}

@Preview
@Composable
private fun ChatScreenErrorPreview() {
    LmStudioAndroidTheme {
        val state = object : State<ChatState> {
            override val value: ChatState
                get() = ChatState(
                    loading = false,
                    models = emptyList(),
                    chat = Chat(emptyList()),
                    fatalError = ChatState.FatalError("A fatal error. A fatal error. A fatal error. A fatal error. A fatal error.", "url")
                )
        }
        ChatScreen(
            state = state,
            messageAction = {},
            saveUrlAction = {},
            selectionModelAction = {},
            reloadModelsAction = {},
            goToPreferencesAction = {},
            requestModelSelectionAction = {},
            clearChatAction = {}
        )
    }
}

@Preview
@Composable
private fun ChatScreenModelsPreview() {
    LmStudioAndroidTheme {
        val state = object : State<ChatState> {
            override val value: ChatState
                get() = ChatState(
                    loading = false,
                    models = listOf(LmModel("model1"), LmModel("model2"), LmModel("model2")),
                    chat = Chat(emptyList())
                )
        }
        ChatScreen(
            state = state,
            messageAction = {},
            saveUrlAction = {},
            selectionModelAction = {},
            reloadModelsAction = {},
            goToPreferencesAction = {},
            requestModelSelectionAction = {},
            clearChatAction = {}
        )
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    LmStudioAndroidTheme {
        val state = object : State<ChatState> {
            override val value: ChatState
                get() = ChatState(
                    loading = false,
                    models = listOf(LmModel("model1")),
                    chat = Chat(listOf(
                        Message(
                            role = Role.USER.name,
                            model = "model1",
                            message = "Message1"
                        )
                    )),
                    selectedModel = "very-very-very-very-long-model-name"
                )
        }
        ChatScreen(
            state = state,
            messageAction = {},
            saveUrlAction = {},
            selectionModelAction = {},
            reloadModelsAction = {},
            goToPreferencesAction = {},
            requestModelSelectionAction = {},
            clearChatAction = {}
        )
    }
}