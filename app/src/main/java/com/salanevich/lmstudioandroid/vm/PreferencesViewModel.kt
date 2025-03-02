package com.salanevich.lmstudioandroid.vm

import androidx.lifecycle.ViewModel
import com.salanevich.data.network.BaseUrlSetter
import com.salanevich.data.usecase.preferences.PreferencesInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.zip
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesInteractor: PreferencesInteractor,
    private val lmStudioAPIImpl: BaseUrlSetter,
) : ViewModel(), ContainerHost<PreferencesState, PreferencesSideEffect> {

    override val container = container<PreferencesState, PreferencesSideEffect>(PreferencesState())

    fun reduce(intent: PreferencesIntent) {
        when (intent) {
            is PreferencesIntent.Apply -> apply(intent.url, intent.system)
            is PreferencesIntent.InitData -> initData()
        }
    }

    private fun initData() = intent {
        reduce { state.copy(isLoading = true) }
        preferencesInteractor.getBaseUrl().zip(preferencesInteractor.getSystemPrompt()) { url, system ->
            reduce { state.copy(url = url, system = system, isLoading = false) }
        }.first()
    }

    private fun apply(url: String, system: String) = intent {
        lmStudioAPIImpl.setUrl(url)
        preferencesInteractor.putBaseUrl(url)
        preferencesInteractor.putSystemPrompt(system)
        postSideEffect(PreferencesSideEffect.GoToChat)
    }

}

sealed interface PreferencesIntent {
    data object InitData : PreferencesIntent
    data class Apply(val url: String, val system: String) : PreferencesIntent
}

data class PreferencesState(
    val isLoading: Boolean = true,
    val url: String? = null,
    val system: String? = null,
)

sealed class PreferencesSideEffect {
    data object GoToChat: PreferencesSideEffect()
}