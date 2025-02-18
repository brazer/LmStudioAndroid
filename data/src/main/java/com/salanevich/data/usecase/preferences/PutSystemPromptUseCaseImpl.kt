package com.salanevich.data.usecase.preferences

import com.salanevich.data.preferences.Preferences
import com.salanevich.domain.usecase.preferences.PutSystemPromptUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PutSystemPromptUseCaseImpl @Inject constructor(
    private val preferences: Preferences
): PutSystemPromptUseCase {
    override suspend fun invoke(prompt: String) {
        preferences.putSystemPrompt(prompt)
    }
}