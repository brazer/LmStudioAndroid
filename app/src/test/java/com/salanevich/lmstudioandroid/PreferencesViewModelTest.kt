package com.salanevich.lmstudioandroid

import com.salanevich.data.network.BaseUrlSetter
import com.salanevich.data.usecase.preferences.PreferencesInteractor
import com.salanevich.domain.usecase.preferences.GetBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.GetSystemPromptUseCase
import com.salanevich.domain.usecase.preferences.PutBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.PutSystemPromptUseCase
import com.salanevich.lmstudioandroid.vm.PreferencesIntent
import com.salanevich.lmstudioandroid.vm.PreferencesSideEffect
import com.salanevich.lmstudioandroid.vm.PreferencesState
import com.salanevich.lmstudioandroid.vm.PreferencesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test

class PreferencesViewModelTest {

    private lateinit var viewModel: PreferencesViewModel
    private val getBaseUrlUseCase = mockk<GetBaseUrlUseCase>()
    private val getSystemPromptUseCase = mockk<GetSystemPromptUseCase>()
    private val putBaseUrlUseCase = mockk<PutBaseUrlUseCase>()
    private val putSystemPromptUseCase = mockk<PutSystemPromptUseCase>()
    private val baseUrlSetter = mockk<BaseUrlSetter>()

    @Before
    fun setUp() {
        viewModel = PreferencesViewModel(preferencesInteractor = PreferencesInteractor(
                getBaseUrlUseCase = getBaseUrlUseCase,
                putBaseUrlUseCase = putBaseUrlUseCase,
                getSystemPromptUseCase = getSystemPromptUseCase,
                putSystemPromptUseCase = putSystemPromptUseCase
            ),
            lmStudioAPIImpl = baseUrlSetter
        )
    }

    @Test
    fun `test loading data`() = runTest {
        coEvery { getBaseUrlUseCase() } returns flow { emit("url") }
        coEvery { getSystemPromptUseCase() } returns flow { emit("prompt") }
        viewModel.test(this, PreferencesState()) {
            expectInitialState()
            containerHost.reduce(PreferencesIntent.InitData)
            expectState { PreferencesState(url = "url", system = "prompt", isLoading = false) }
        }
    }

    @Test
    fun `test apply`() = runTest {
        coEvery { putBaseUrlUseCase("url") } returns Unit
        coEvery { putSystemPromptUseCase("prompt") } returns Unit
        every { baseUrlSetter.setUrl("url") } returns Unit
        viewModel.test(this, PreferencesState()) {
            expectInitialState()
            containerHost.reduce(PreferencesIntent.Apply("url", "prompt"))
            expectSideEffect(PreferencesSideEffect.GoToChat)
        }
    }

}