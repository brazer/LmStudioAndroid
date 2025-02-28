package com.salanevich.data.network

import android.util.Log
import com.salanevich.data.R
import com.salanevich.data.network.body.LmModelsResponse
import com.salanevich.data.network.body.ChatResponse
import com.salanevich.domain.model.Chat
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LmStudioAPIImpl @Inject constructor(): LmStudioAPI, BaseUrlSetter {

    private val client : HttpClient by lazy {
        HttpClient(OkHttp) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 60000
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    private lateinit var baseUrl: String

    override suspend fun getLmModels(): NetworkResult<LmModelsResponse> {
        return try {
            client.get("$baseUrl/v1/models").toResult()
        } catch (e: Exception) {
            Log.e("LmStudioAPIImpl", "getLmModels", e)
            NetworkResult.Error(NetworkException(e, R.string.network_error))
        }
    }

    override suspend fun getMessage(
        chat: Chat, model: String, maxTokens: Int, temperature: Float, stream: Boolean
    ): NetworkResult<ChatResponse> {
        return try {
            client.post("$baseUrl/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                setBody(chat.map(model, maxTokens, temperature, stream))
            }.toResult()
        } catch (e: Exception) {
            Log.e("LmStudioAPIImpl", "getMessage", e)
            NetworkResult.Error(NetworkException(e, R.string.network_error))
        }
    }

    override fun setUrl(url: String) {
        baseUrl = url
    }

}
