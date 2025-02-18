package com.salanevich.data.network

import androidx.annotation.StringRes
import com.salanevich.data.R
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T : Any> HttpResponse.toResult(): NetworkResult<T> {
    return when (status.value) {
        200 -> NetworkResult.Success(body())
        400, 401 -> NetworkResult.Error(NetworkException("Incorrect request!", R.string.incorrect_request))
        500, 503 -> NetworkResult.Error(NetworkException("Server error!", R.string.server_error))
        504 -> NetworkResult.Error(NetworkException("Too much load at this time, try again later!", R.string.too_much_load))
        else -> NetworkResult.Error(NetworkException("Something went wrong! Please try again.", R.string.something_went_wrong))
    }
}

class NetworkException: Exception {
    val stringId: Int
    constructor(message: String, @StringRes stringId: Int): super(message) {
        this.stringId = stringId
    }
    constructor(exception: Exception, @StringRes stringId: Int): super(exception) {
        this.stringId = stringId
    }
}

sealed interface NetworkResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>
    data class Error<out T : Any>(val error: Exception) : NetworkResult<T>
}