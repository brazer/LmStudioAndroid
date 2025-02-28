package com.salanevich.lmstudioandroid

import android.content.Context
import android.util.Log
import com.salanevich.data.network.BaseUrlSetter
import com.salanevich.data.network.NetworkException
import com.salanevich.data.network.body.Role
import com.salanevich.data.usecase.preferences.PreferencesInteractor
import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.LmModel
import com.salanevich.domain.model.Message
import com.salanevich.domain.model.SpeechState
import com.salanevich.domain.usecase.GetMessageUseCase
import com.salanevich.domain.usecase.GetModelsUseCase
import com.salanevich.domain.usecase.ListenUseCase
import com.salanevich.domain.usecase.preferences.GetBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.GetSystemPromptUseCase
import com.salanevich.domain.usecase.preferences.PutBaseUrlUseCase
import com.salanevich.lmstudioandroid.vm.ChatIntent
import com.salanevich.lmstudioandroid.vm.ChatSideEffect
import com.salanevich.lmstudioandroid.vm.ChatState
import com.salanevich.lmstudioandroid.vm.ChatViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test

class ChatViewModelTest {

    private lateinit var viewModel: ChatViewModel
    private val baseUrlSetter = mockk<BaseUrlSetter>()
    private val getBaseUrlUseCase = mockk<GetBaseUrlUseCase>()
    private val getModelsUseCase = mockk<GetModelsUseCase>()
    private val getMessageUseCase = mockk<GetMessageUseCase>()
    private val getSystemPromptUseCase = mockk<GetSystemPromptUseCase>()
    private val listenUseCase = mockk<ListenUseCase>()
    private val context = mockk<Context>()

    @Before
    fun setUp() {
        viewModel = ChatViewModel(
            getModelsUseCase = getModelsUseCase,
            getMessageUseCase = getMessageUseCase,
            preferencesInteractor = PreferencesInteractor(
                getBaseUrlUseCase = getBaseUrlUseCase,
                putBaseUrlUseCase = FakePutBaseUrlUseCase(),
                getSystemPromptUseCase = getSystemPromptUseCase,
                putSystemPromptUseCase = mockk()
            ),
            baseUrlSetter = baseUrlSetter,
            listenUseCase = listenUseCase,
            context = context
        )
    }

    @Test
    fun `successful request base url and load models`() = runTest {
        coEvery { getBaseUrlUseCase() } returns flow { emit("") }
        every { baseUrlSetter.setUrl(any()) } returns Unit
        val models = listOf(LmModel("test"))
        coEvery { getModelsUseCase() } returns models
        viewModel.test(this, ChatState()) {
            expectInitialState()
            containerHost.reduce(ChatIntent.LoadBaseUrl)
            expectState { ChatState(requestUrl = true, loading = false) }
            containerHost.reduce(ChatIntent.PutBaseUrl("https://example.com"))
            expectState { ChatState(requestUrl = false, loading = false) }
            // load models
            expectState { ChatState(requestUrl = false, loading = true) }
            expectState { ChatState(models = models, fatalError = null, loading = false) }
        }
    }

    @Test
    fun `server is unreachable for loading models`() = runTest {
        coEvery { getModelsUseCase() } throws NetworkException("Server is unreachable", 0)
        every { context.getString(any()) } returns "Server is unreachable"
        coEvery { getBaseUrlUseCase() } returns flow { emit("") }
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        viewModel.test(this, ChatState()) {
            expectInitialState()
            containerHost.reduce(ChatIntent.LoadModels)
            expectState { ChatState(loading = false, fatalError = ChatState.FatalError("Server is unreachable", "")) }
        }
    }

    @Test
    fun `server is unreachable for getting response`() = runTest {
        val chat = Chat(listOf(Message("model", Role.SYSTEM.value, "message1")))
        coEvery { getMessageUseCase(any(), any()) } throws NetworkException("Server is unreachable", 0)
        every { context.getString(any()) } returns "Server is unreachable"
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        val initSate = ChatState(chat = chat, selectedModel = "model")
        viewModel.test(this, initSate) {
            expectInitialState()
            containerHost.reduce(ChatIntent.GetResponse("message2"))
            val messages = chat.messages.toMutableList().apply { add(Message("model", Role.USER.value, "message2")) }
            val c = chat.copy(messages = messages)
            expectState { initSate.copy(loadingOfMessage = true, chat = c) }
            expectState { initSate.copy(loadingOfMessage = false, chat = c) }
            expectSideEffect(ChatSideEffect.ShowErrorMessage("Server is unreachable"))
        }
    }

    @Test
    fun `successful getting response without system prompt`() = runTest {
        val message2 = Message("model", Role.USER.value, "message2")
        val message3 = Message("model", Role.ASSISTANT.value, "message3")
        coEvery { getMessageUseCase(any(), any()) } returns message3
        val chat = Chat(listOf(Message("model", Role.SYSTEM.value, "message1")))
        val initSate = ChatState(chat = chat, selectedModel = "model")
        viewModel.test(this, initSate) {
            expectInitialState()
            containerHost.reduce(ChatIntent.GetResponse("message2"))
            val messages = chat.messages.toMutableList().apply { add(message2) }
            var c = chat.copy(messages = messages)
            expectState { initSate.copy(loadingOfMessage = true, chat = c) }
            c = c.copy(messages = c.messages.toMutableList().apply { add(message3) })
            expectState { initSate.copy(loadingOfMessage = false, chat = c) }
        }
    }

    @Test
    fun `successful getting response with system prompt`() = runTest {
        val systemPrompt = Message("model", Role.SYSTEM.value, "message1")
        coEvery { getSystemPromptUseCase() } returns flow { emit("message1") }
        val userMessage = Message("model", Role.USER.value, "message2")
        val assistMessage = Message("model", Role.ASSISTANT.value, "message3")
        coEvery { getMessageUseCase(any(), any()) } returns assistMessage
        val chat = Chat(emptyList())
        val initSate = ChatState(chat = chat, selectedModel = "model")
        viewModel.test(this, initSate) {
            expectInitialState()
            containerHost.reduce(ChatIntent.GetResponse("message2"))
            val messages = chat.messages.toMutableList().apply {
                add(systemPrompt)
                add(userMessage)
            }
            var c = chat.copy(messages = messages)
            expectState { initSate.copy(loadingOfMessage = true, chat = c) }
            c = c.copy(messages = c.messages.toMutableList().apply { add(assistMessage) })
            expectState { initSate.copy(loadingOfMessage = false, chat = c) }
        }
    }

    @Test
    fun `unsuccessful getting response due to the same user message`() = runTest {
        val userMessage = Message("model", Role.USER.value, "message1")
        val chat = Chat(listOf(userMessage))
        val initSate = ChatState(chat = chat, selectedModel = "model")
        viewModel.test(this, initSate) {
            expectInitialState()
            containerHost.reduce(ChatIntent.GetResponse("message1"))
            expectSideEffect(ChatSideEffect.ShowErrorMessage("You already sent this message"))
        }
    }

    @Test
    fun `test clear chat`() = runTest {
        val chat = Chat(listOf(Message("model", Role.USER.value, "message1")))
        val initSate = ChatState(chat = chat)
        viewModel.test(this, initSate) {
            expectInitialState()
            containerHost.reduce(ChatIntent.ClearChat)
            expectState { initSate.copy(chat = null) }
        }
    }

    @Test
    fun `test request model selection`() = runTest {
        viewModel.test(this, ChatState(selectedModel = "model1")) {
            expectInitialState()
            containerHost.reduce(ChatIntent.RequestModelSelection)
            expectState { ChatState(selectedModel = null) }
            containerHost.reduce(ChatIntent.SelectModel("model2"))
            expectState { ChatState(selectedModel = "model2") }
        }
    }

    @Test
    fun `test go to preferences`() = runTest {
        viewModel.test(this, ChatState()) {
            expectInitialState()
            containerHost.reduce(ChatIntent.GoToPreferences)
            expectSideEffect(ChatSideEffect.GoToPreferences)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test on mic clicked`() = runTest {
        coEvery { listenUseCase() } returns flow {
            emit(SpeechState.Start)
            delay(100)
            emit(SpeechState.Text("message"))
            delay(100)
            emit(SpeechState.End)
        }
        viewModel.test(this, ChatState()) {
            Dispatchers.setMain(Dispatchers.Unconfined)
            expectInitialState()
            containerHost.reduce(ChatIntent.OnMicLicked)
            expectState { ChatState(isMicOn = true, recognizedText = "Listening...") }
            expectState { ChatState(isMicOn = true, recognizedText = "message") }
            expectState { ChatState(isMicOn = false, recognizedText = "message") }
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `test switch off mic`() = runTest {
        viewModel.test(this, ChatState(isMicOn = true, recognizedText = "text")) {
            expectInitialState()
            containerHost.reduce(ChatIntent.OnMicLicked)
            expectState { ChatState(isMicOn = false, recognizedText = "") }
        }
    }

    private class FakePutBaseUrlUseCase : PutBaseUrlUseCase {
        override suspend fun invoke(url: String) {}
    }

}