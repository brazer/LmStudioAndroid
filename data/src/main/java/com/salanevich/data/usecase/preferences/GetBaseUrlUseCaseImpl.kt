package com.salanevich.data.usecase.preferences

import com.salanevich.data.preferences.Preferences
import com.salanevich.domain.usecase.preferences.GetBaseUrlUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBaseUrlUseCaseImpl @Inject constructor(
    private val preferences: Preferences
): GetBaseUrlUseCase {
    override suspend fun invoke(): Flow<String> {
        return preferences.getBaseUrl()
    }
}