package com.salanevich.data.network.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Role(val value: String) {
    @SerialName("user") USER("user"),
    @SerialName("assistant") ASSISTANT("assistant"),
    @SerialName("system") SYSTEM("system");
    companion object {
        fun fromValue(value: String): Role = when (value) {
            "user" -> USER
            "assistant" -> ASSISTANT
            "system" -> SYSTEM
            else -> throw IllegalArgumentException("Invalid Role value: $value")
        }
    }
}