package com.salanevich.domain.model

data class Chat(
    val messages: List<Message>
)

data class Message(
    val model: String,
    val role: String,
    val message: String
)