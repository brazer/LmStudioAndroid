package com.salanevich.domain.usecase

import com.salanevich.domain.model.SpeechState
import kotlinx.coroutines.flow.Flow

interface ListenUseCase {
    operator fun invoke(): Flow<SpeechState>
}