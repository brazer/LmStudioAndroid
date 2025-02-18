package com.salanevich.data.usecase.preferences

import com.salanevich.data.preferences.Preferences
import com.salanevich.domain.usecase.preferences.GetSystemPromptUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSystemPromptUseCaseImpl @Inject constructor(
    private val preferences: Preferences
): GetSystemPromptUseCase {
    override suspend fun invoke(): Flow<String> {
        return preferences.getSystemPrompt()
    }
}