package com.pranshulgg.weather_master_app.core.model.domain

import com.pranshulgg.weather_master_app.R
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

sealed class AppException(message: String? = null) : Exception(message) {
    class Network : AppException()
    class CurrentLocationUnavailable : AppException()
    class BadRequest : AppException()
    class Unauthorized : AppException()
    class Forbidden : AppException()
    class NotFound : AppException()
    class RequestTimeout : AppException()
    class TooManyRequests : AppException()
    class Server : AppException()

    class Unknown : AppException()

    class NoApiKeyError : AppException()

    // A key was provided but the provider rejected it (invalid, revoked, or expired) -
    // sources can't tell those apart from an HTTP 401 alone, so the message stays generic.
    class ApiKeyRejectedError : AppException()

    class SecureConnection : AppException()

    class EmptyResponseBody : AppException()

    class BackupSchemaVersionUnsupported : AppException()
    class BackupFileCorrupted : AppException()
    class BackupFileIOError : AppException()
    class BackupMissingDefaultLocation : AppException()

}

fun AppException.toMessageRes(): Int {
    return when (this) {
        is AppException.Network -> R.string.error_network
        is AppException.CurrentLocationUnavailable -> R.string.current_location_not_found
        is AppException.BadRequest -> R.string.error_bad_request
        is AppException.Unauthorized -> R.string.error_unauthorized
        is AppException.Forbidden -> R.string.error_forbidden
        is AppException.NotFound -> R.string.error_not_found
        is AppException.RequestTimeout -> R.string.error_request_timeout
        is AppException.TooManyRequests -> R.string.error_too_many_requests
        is AppException.Server -> R.string.error_server
        is AppException.Unknown -> R.string.error_generic
        is AppException.NoApiKeyError -> R.string.error_no_api_key
        is AppException.ApiKeyRejectedError -> R.string.error_api_key_rejected
        is AppException.SecureConnection -> R.string.error_secure_connection_failed
        is AppException.EmptyResponseBody -> R.string.error_empty_response_body
        is AppException.BackupSchemaVersionUnsupported -> R.string.error_backup_schema_unsupported
        is AppException.BackupFileCorrupted -> R.string.error_backup_file_corrupted
        is AppException.BackupFileIOError -> R.string.error_backup_file_io
        is AppException.BackupMissingDefaultLocation -> R.string.error_backup_missing_default_location
    }
}

fun Throwable.toAppException(): AppException {
    if (this is CancellationException) throw this

    return when (this) {
        is AppException -> this

        is SSLHandshakeException -> AppException.SecureConnection()
        is SSLPeerUnverifiedException -> AppException.SecureConnection()

        is UnknownHostException -> AppException.Network()
        is ConnectException -> AppException.Network()
        is NoRouteToHostException -> AppException.Network()
        is SocketException -> AppException.Network()

        is SocketTimeoutException -> AppException.RequestTimeout()

        is IOException -> AppException.Network()

        is HttpException -> when (this.code()) {
            400 -> AppException.BadRequest()
            401 -> AppException.Unauthorized()
            403 -> AppException.Forbidden()
            404 -> AppException.NotFound()
            408 -> AppException.RequestTimeout()
            429 -> AppException.TooManyRequests()
            in 500..599 -> AppException.Server()
            else -> AppException.Unknown()
        }


        else -> AppException.Unknown()
    }
}