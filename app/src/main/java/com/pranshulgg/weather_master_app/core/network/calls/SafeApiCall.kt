package com.pranshulgg.weather_master_app.core.network.calls

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


suspend fun <T> safeApiCall(
    call: suspend () -> Response<T>
): Result<T> {
    return try {
        val response = call()

        if (response.isSuccessful) {
            response.body()?.let(Result.Companion::success)
                ?: Result.failure(
                    AppException.EmptyResponseBody()
                )
        } else {
            Result.failure(
                resolveException(response)
            )
        }
    } catch (e: Exception) {
        Result.failure(e.toAppException())
    }
}

private fun resolveException(response: Response<*>): AppException {
    return when (response.code()) {
        400 -> AppException.BadRequest()
        401 -> AppException.Unauthorized()
        403 -> AppException.Forbidden()
        404 -> AppException.NotFound()
        408 -> AppException.RequestTimeout()
        429 -> AppException.TooManyRequests()
        in 500..599 -> AppException.Server()
        else -> AppException.Unknown()
    }
}