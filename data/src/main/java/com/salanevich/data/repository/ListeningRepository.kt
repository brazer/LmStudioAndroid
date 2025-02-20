package com.salanevich.data.repository

import com.salanevich.domain.model.SpeechState
import kotlinx.coroutines.flow.Flow

interface ListeningRepository {
    fun listen(): Flow<SpeechState>
}