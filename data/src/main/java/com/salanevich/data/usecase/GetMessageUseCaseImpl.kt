package com.salanevich.data.usecase

import com.salanevich.data.repository.LmStudioRepository
import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.Message
import com.salanevich.domain.usecase.GetMessageUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMessageUseCaseImpl @Inject constructor(
    private val repo: LmStudioRepository
) : GetMessageUseCase {
    override suspend fun invoke(previousChat: Chat, model: String): Message {
        return repo.getMessage(previousChat, model)
    }
}