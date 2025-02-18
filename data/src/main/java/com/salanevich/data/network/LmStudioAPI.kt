package com.salanevich.data.network

import com.salanevich.data.network.body.ChatResponse
import com.salanevich.data.network.body.LmModelsResponse
import com.salanevich.domain.model.Chat

interface LmStudioAPI {
    suspend fun getLmModels(): NetworkResult<LmModelsResponse>
    suspend fun getMessage(
        chat: Chat, model: String, maxTokens: Int = -1, temperature: Float = 0.7f, stream: Boolean = false
    ): NetworkResult<ChatResponse>
}