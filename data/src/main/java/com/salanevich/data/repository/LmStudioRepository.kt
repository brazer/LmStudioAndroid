package com.salanevich.data.repository

import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.LmModel
import com.salanevich.domain.model.Message

interface LmStudioRepository {
    suspend fun getModels(): List<LmModel>
    suspend fun getMessage(
        previousChat: Chat, model: String, maxTokens: Int = -1, temperature: Float = 0.7f, stream: Boolean = false
    ): Message
}