package com.salanevich.lmstudioandroid.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.salanevich.data.network.body.Role
import com.salanevich.data.network.BaseUrlSetter
import com.salanevich.data.network.NetworkException
import com.salanevich.data.usecase.preferences.PreferencesInteractor
import com.salanevich.domain.model.Chat
import com.salanevich.domain.usecase.GetModelsUseCase
import com.salanevich.domain.model.LmModel
import com.salanevich.domain.model.Message
import com.salanevich.domain.model.SpeechState
import com.salanevich.domain.usecase.GetMessageUseCase
import com.salanevich.domain.usecase.ListenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getModelsUseCase: GetModelsUseCase,
    private val getMessageUseCase: GetMessageUseCase,
    private val preferencesInteractor: PreferencesInteractor,
    private val lmStudioAPIImpl: BaseUrlSetter,
    private val listenUseCase: ListenUseCase,
    @ApplicationContext private val context: Context
): ViewModel(), ContainerHost<ChatState, ChatSideEffect> {

    override val container = container<ChatState, ChatSideEffect>(ChatState()) {
        reduce(ChatIntent.LoadBaseUrl)
    }

    fun reduce(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.GetResponse -> getMessage(intent.message)
            ChatIntent.LoadBaseUrl -> loadBaseUrl()
            is ChatIntent.PutBaseUrl -> putBaseUrl(intent.url)
            is ChatIntent.SelectModel -> selectModel(intent.model)
            ChatIntent.LoadModels -> loadModels()
            is ChatIntent.GoToPreferences -> navigateToPreferences()
            ChatIntent.RequestModelSelection -> requestModelSelection()
            ChatIntent.ClearChat -> clearChat()
            ChatIntent.OnMicLicked -> onMicClicked()
        }
    }

    private fun onMicClicked() = intent {
        if (!state.isMicOn) {
            withContext(Dispatchers.Main) {
                listenUseCase().collectLatest {
                    when (it) {
                        SpeechState.Initialization -> {
                            reduce { state.copy(isMicOn = true, recognizedText = "Initialization...") }
                        }
                        SpeechState.Start -> {
                            reduce { state.copy(isMicOn = true, recognizedText = "Speak") }
                        }
                        SpeechState.Speaking -> {
                            reduce { state.copy(recognizedText = "Listening...") }
                        }
                        SpeechState.End -> {
                            reduce { state.copy(isMicOn = false, recognizedText = "") }
                        }
                        is SpeechState.Text -> {
                            reduce { state.copy(recognizedText = it.value) }
                        }
                    }
                }
            }
        } else reduce { state.copy(isMicOn = false, recognizedText = "") }
    }

    private fun clearChat() = intent {
        reduce { state.copy(chat = null) }
    }

    private fun requestModelSelection() = intent {
        reduce { state.copy(selectedModel = null) }
    }

    private fun navigateToPreferences() = intent {
        postSideEffect(ChatSideEffect.GoToPreferences)
    }

    private fun selectModel(model: String) = intent {
        reduce { state.copy(selectedModel = model) }
    }

    private fun putBaseUrl(url: String) = intent {
        preferencesInteractor.putBaseUrl(url)
        lmStudioAPIImpl.setUrl(url)
        reduce { state.copy(requestUrl = false) }
        loadModels()
    }

    private fun loadBaseUrl() = intent {
        val url = preferencesInteractor.getBaseUrl().first()
        if (url.isEmpty()) {
            reduce { state.copy(requestUrl = true, loading = false) }
        } else {
            lmStudioAPIImpl.setUrl(url)
            loadModels()
        }
    }

    private fun loadModels() = intent {
        reduce { state.copy(loading = true) }
        try {
            val models = getModelsUseCase()
            reduce { state.copy(models = models, fatalError = null, loading = false) }
        } catch (e: NetworkException) {
            Log.e("ChatViewModel", "loadModels: ${e.message}")
            val url = preferencesInteractor.getBaseUrl().first()
            reduce { state.copy(loading = false, fatalError = ChatState.FatalError(context.getString(e.stringId), url)) }
        }
    }

    private fun getMessage(message: String) = intent {
        reduce { state.copy(recognizedText = "") }
        val previousMessages = state.chat?.messages?.toMutableList() ?: mutableListOf()
        val oldSystemPrompt = previousMessages.find { it.role == Role.SYSTEM.value } ?: ""
        val newSystemPrompt = preferencesInteractor.getSystemPrompt().first()
        if (newSystemPrompt != oldSystemPrompt && newSystemPrompt.isNotEmpty()) {
                previousMessages.add(Message(
                    model = checkNotNull(state.selectedModel),
                    role = Role.SYSTEM.value,
                    message = newSystemPrompt
                ))
        } else if (newSystemPrompt.isEmpty()) {
            previousMessages.removeIf { it.role == Role.SYSTEM.value }
        }
        previousMessages.add(Message(
            model = checkNotNull(state.selectedModel),
            role = Role.USER.value,
            message = message
        ))
        val previousChat = Chat(previousMessages)
        reduce { state.copy(loadingOfMessage = true, chat = previousChat) }
        try {
            val m = getMessageUseCase(
                previousChat = previousChat,
                model = checkNotNull(state.selectedModel)
            )
            val messages = previousChat.messages.toMutableList()
            messages.add(m)
            val chat = previousChat.copy(messages = messages)
            reduce { state.copy(loadingOfMessage = false, chat = chat) }
        } catch (e: NetworkException) {
            Log.e("ChatViewModel", "getMessage: ${e.message}")
            reduce { state.copy(loadingOfMessage = false) }
            postSideEffect(ChatSideEffect.ShowErrorMessage(context.getString(e.stringId)))
        }
    }

}

sealed interface ChatIntent {
    data object LoadBaseUrl: ChatIntent
    data class PutBaseUrl(val url: String): ChatIntent
    data class GetResponse(val message: String): ChatIntent
    data class SelectModel(val model: String): ChatIntent
    data object LoadModels: ChatIntent
    data object GoToPreferences: ChatIntent
    data object RequestModelSelection : ChatIntent
    data object ClearChat : ChatIntent
    data object OnMicLicked : ChatIntent
}

data class ChatState(
    val loading: Boolean = true,
    val loadingOfMessage: Boolean = false,
    val models: List<LmModel> = emptyList(),
    val selectedModel: String? = null,
    val chat: Chat? = null,
    val fatalError: FatalError? = null,
    val requestUrl: Boolean = false,
    val isMicOn: Boolean = false,
    val recognizedText: String = ""
) {
    data class FatalError(val message: String, val url: String)
}

sealed class ChatSideEffect {
    data class ShowErrorMessage(val error: String): ChatSideEffect()
    data object GoToPreferences: ChatSideEffect()
}