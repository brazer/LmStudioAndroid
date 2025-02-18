package com.salanevich.domain.usecase

import com.salanevich.domain.model.LmModel

interface GetModelsUseCase {
    suspend operator fun invoke(): List<LmModel>
}