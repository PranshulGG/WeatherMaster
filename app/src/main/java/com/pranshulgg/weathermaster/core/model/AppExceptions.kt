package com.pranshulgg.weathermaster.core.model

import com.pranshulgg.weathermaster.R

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