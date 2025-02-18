package com.salanevich.data.repository

import com.salanevich.data.network.LmStudioAPI
import com.salanevich.data.network.map
import com.salanevich.data.network.NetworkResult
import com.salanevich.domain.model.Chat
import com.salanevich.domain.model.LmModel
import com.salanevich.domain.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LmStudioRepositoryImpl @Inject constructor(
    private val webApi: LmStudioAPI
): LmStudioRepository {

    override suspend fun getModels(): List<LmModel>  {
        return when (val result = webApi.getLmModels()) {
            is NetworkResult.Error -> throw result.error
            is NetworkResult.Success -> result.data.map()
        }
    }

    override suspend fun getMessage(
        previousChat: Chat, model: String, maxTokens: Int, temperature: Float, stream: Boolean
    ): Message = withContext(Dispatchers.IO) {
        return@withContext when (val result = webApi.getMessage(previousChat, model)) {
            is NetworkResult.Error -> throw result.error
            is NetworkResult.Success -> result.data.map()
        }
    }

}