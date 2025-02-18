package com.salanevich.data.network

import com.salanevich.data.network.body.ChatRequest
import com.salanevich.data.network.body.ChatResponse
import com.salanevich.data.network.body.LmModelsResponse
import com.salanevich.data.network.body.MessagesItem
import com.salanevich.data.network.body.Role
import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.LmModel
import com.salanevich.domain.model.Message

fun LmModelsResponse.map(): List<LmModel> = data.map {
    LmModel(
        name = it.id
    )
}

fun Chat.map(model: String, maxTokens: Int, temperature: Float, stream: Boolean): ChatRequest {
    return ChatRequest(
        model = model,
        maxTokens = maxTokens,
        temperature = temperature,
        stream = stream,
        messages = messages.map {
            MessagesItem(
                role = Role.fromValue(it.role),
                content = it.message
            )
        }
    )
}

fun ChatResponse.map(): Message {
    val m = choices.last().message
    return Message(
        model = model,
        role = m.role.value,
        message = m.content
    )
}