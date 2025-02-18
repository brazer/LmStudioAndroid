package com.salanevich.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : Preferences {

    private val Context.dataStore by preferencesDataStore("preferences")
    private val baseUrl = stringPreferencesKey("base_url")
    private val systemPrompt = stringPreferencesKey("system_prompt")

    override fun getBaseUrl(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[baseUrl] ?: ""
        }
    }

    override suspend fun putBaseUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[baseUrl] = url
        }
    }

    override fun getSystemPrompt(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[systemPrompt] ?: ""
        }
    }

    override suspend fun putSystemPrompt(prompt: String) {
        context.dataStore.edit { preferences ->
            preferences[systemPrompt] = prompt
        }
    }

}