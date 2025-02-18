package com.salanevich.data.network.body

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatRequest(

	@SerialName("max_tokens")
	val maxTokens: Int,

	@SerialName("stream")
	val stream: Boolean,

	@SerialName("temperature")
	val temperature: Float,

	@SerialName("messages")
	val messages: List<MessagesItem>,

	@SerialName("model")
	val model: String
)

@Serializable
data class MessagesItem(

	@SerialName("role")
	val role: Role,

	@SerialName("content")
	val content: String
)
