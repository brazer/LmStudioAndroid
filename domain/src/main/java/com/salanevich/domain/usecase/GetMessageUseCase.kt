package com.salanevich.domain.usecase

import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.Message

interface GetMessageUseCase {
    suspend operator fun invoke(previousChat: Chat, model: String): Message
}