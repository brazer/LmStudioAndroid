package com.salanevich.domain.usecase.preferences

import kotlinx.coroutines.flow.Flow

interface GetSystemPromptUseCase {
    suspend operator fun invoke(): Flow<String>
}