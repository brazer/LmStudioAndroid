package com.salanevich.data.usecase.preferences

import com.salanevich.data.preferences.Preferences
import com.salanevich.domain.usecase.preferences.PutBaseUrlUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PutBaseUrlUseCaseImpl @Inject constructor(
    private val preferences: Preferences
) : PutBaseUrlUseCase {
    override suspend fun invoke(url: String) {
        preferences.putBaseUrl(url)
    }
}