package com.salanevich.domain.usecase.preferences

import kotlinx.coroutines.flow.Flow

interface GetBaseUrlUseCase {
    suspend operator fun invoke(): Flow<String>
}