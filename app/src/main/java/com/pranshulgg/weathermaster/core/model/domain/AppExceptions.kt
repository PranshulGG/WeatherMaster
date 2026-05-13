package com.pranshulgg.weathermaster.core.model.domain

import coil.network.HttpException
import com.pranshulgg.weathermaster.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

sealed class AppException(message: String? = null) : Exception(message) {
    class Network : AppException()
    class CurrentLocationUnavailable : AppException()
    class Server : AppException()
    class Unknown : AppException()

}

fun AppException.toMessageRes(): Int {
    return when (this) {
        is AppException.Network -> R.string.error_network
        is AppException.CurrentLocationUnavailable -> R.string.current_location_not_found
        is AppException.Server -> R.string.error_server
        is AppException.Unknown -> R.string.error_generic
    }
}

fun Throwable.toAppException(): AppException {
    if (this is CancellationException) throw this

    return when (this) {
        is UnknownHostException,
        is SocketTimeoutException,
        is IOException -> AppException.Network()

        is HttpException -> AppException.Server()

        else -> AppException.Unknown()
    }
}