package com.salanevich.data.usecase

import com.salanevich.data.repository.LmStudioRepository
import com.salanevich.domain.usecase.GetModelsUseCase
import com.salanevich.domain.model.LmModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetModelsUseCaseImpl @Inject constructor(
    private val repo: LmStudioRepository
): GetModelsUseCase {
    override suspend fun invoke(): List<LmModel> {
        return repo.getModels()
    }
}