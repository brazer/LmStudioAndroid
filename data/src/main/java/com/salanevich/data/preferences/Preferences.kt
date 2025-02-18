package com.salanevich.data.preferences

import kotlinx.coroutines.flow.Flow

interface Preferences {
    fun getBaseUrl(): Flow<String>
    suspend fun putBaseUrl(url: String)
    fun getSystemPrompt(): Flow<String>
    suspend fun putSystemPrompt(prompt: String)
}