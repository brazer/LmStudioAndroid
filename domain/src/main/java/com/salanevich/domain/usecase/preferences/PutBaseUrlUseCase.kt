package com.salanevich.domain.usecase.preferences

interface PutBaseUrlUseCase {
    suspend operator fun invoke(url: String)
}