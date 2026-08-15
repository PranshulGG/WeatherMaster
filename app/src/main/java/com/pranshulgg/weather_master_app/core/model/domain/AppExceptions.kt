package com.pranshulgg.weather_master_app.core.model.domain

import coil.network.HttpException
import com.pranshulgg.weather_master_app.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

sealed class AppException(message: String? = null) : Exception(message) {
    class Network : AppException()
    class CurrentLocationUnavailable : AppException()
    class Server : AppException()
    class Unknown : AppException()

    class NoApiKeyError : AppException()

    // A key was provided but the provider rejected it (invalid, revoked, or expired) -
    // sources can't tell those apart from an HTTP 401 alone, so the message stays generic.
    class ApiKeyRejectedError : AppException()

}

fun AppException.toMessageRes(): Int {
    return when (this) {
        is AppException.Network -> R.string.error_network
        is AppException.CurrentLocationUnavailable -> R.string.current_location_not_found
        is AppException.Server -> R.string.error_server
        is AppException.Unknown -> R.string.error_generic
        is AppException.NoApiKeyError -> R.string.error_no_api_key
        is AppException.ApiKeyRejectedError -> R.string.error_api_key_rejected
    }
}

fun Throwable.toAppException(): AppException {
    if (this is CancellationException) throw this

    return when (this) {
        is AppException -> this
        is UnknownHostException,
        is SocketTimeoutException,
        is IOException -> AppException.Network()

        is HttpException -> AppException.Server()


        else -> AppException.Unknown()
    }
}