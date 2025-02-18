package com.salanevich.data.network.body

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatResponse(

	@SerialName("created")
	val created: Int,

	@SerialName("usage")
	val usage: Usage,

	@SerialName("model")
	val model: String,

	@SerialName("id")
	val id: String,

	@SerialName("choices")
	val choices: List<ChoicesItem>,

	@SerialName("system_fingerprint")
	val systemFingerprint: String,

	@SerialName("object")
	val _object: String
)

@Serializable
data class Message(

	@SerialName("role")
	val role: Role,

	@SerialName("content")
	val content: String
)

@Serializable
data class Usage(

	@SerialName("completion_tokens")
	val completionTokens: Int,

	@SerialName("prompt_tokens")
	val promptTokens: Int,

	@SerialName("total_tokens")
	val totalTokens: Int
)

@Serializable
data class ChoicesItem(

	@SerialName("finish_reason")
	val finishReason: String,

	@SerialName("index")
	val index: Int,

	@SerialName("message")
	val message: Message,

	@SerialName("logprobs")
	val logprobs: String? = null
)
