package com.salanevich.data.usecase.preferences

import com.salanevich.domain.usecase.preferences.GetBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.GetSystemPromptUseCase
import com.salanevich.domain.usecase.preferences.PutBaseUrlUseCase
import com.salanevich.domain.usecase.preferences.PutSystemPromptUseCase
import javax.inject.Inject

class PreferencesInteractor @Inject constructor(
    private val getBaseUrlUseCase: GetBaseUrlUseCase,
    private val putBaseUrlUseCase: PutBaseUrlUseCase,
    private val getSystemPromptUseCase: GetSystemPromptUseCase,
    private val putSystemPromptUseCase: PutSystemPromptUseCase
) {
    suspend fun getBaseUrl() = getBaseUrlUseCase()
    suspend fun putBaseUrl(url: String) = putBaseUrlUseCase(url)
    suspend fun getSystemPrompt() = getSystemPromptUseCase()
    suspend fun putSystemPrompt(prompt: String) = putSystemPromptUseCase(prompt)
}