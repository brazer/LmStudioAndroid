package com.salanevich.domain.usecase.preferences

interface PutSystemPromptUseCase {
    suspend operator fun invoke(prompt: String)
}