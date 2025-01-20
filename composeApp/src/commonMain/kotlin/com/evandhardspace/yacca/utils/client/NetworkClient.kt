package com.evandhardspace.yacca.utils.client

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.data.datasources.TokenDataSource
import com.evandhardspace.yacca.data.network.auth
import com.evandhardspace.yacca.presentation.SessionEffect
import com.evandhardspace.yacca.presentation.SessionRepository
import com.evandhardspace.yacca.response.AuthResponse
import com.evandhardspace.yacca.response.RefreshRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import org.lighthousegames.logging.logging

private const val UnauthorizedCode = 401

internal class NetworkClient(
    private val client: HttpClient,
    private val tokenDataSource: TokenDataSource,
    private val sessionRepository: SessionRepository,
) {
    suspend inline fun <reified Response> get(
        urlString: String,
        withAuth: Boolean = false,
    ): NetworkResult<Response> = executeRequest<Response> {
        logging.d { "GET request invoked" }
        client.get(urlString) {
            if (withAuth) auth(tokenDataSource)
        }
    }.getOrElse { NetworkResult.Error(it) }

    suspend inline fun <reified Request, reified Response> post(
        urlString: String,
        body: Request? = null,
        withAuth: Boolean = false,
    ): NetworkResult<Response> = executeRequest<Response> {
        logging.d { "POST request invoked" }
        client.post(urlString) {
            body?.let { setBody(it) }
            if (withAuth) auth(tokenDataSource)
        }
    }.getOrElse { NetworkResult.Error(it) }

    suspend inline fun <reified Response> delete(
        urlString: String,
        withAuth: Boolean = false,
    ): NetworkResult<Response> = executeRequest<Response> {
        logging.d { "DELETE request invoked" }
        client.delete(urlString) {
            if (withAuth) auth(tokenDataSource)
        }
    }.getOrElse { NetworkResult.Error(it) }

    private suspend inline fun <reified Response> executeRequest(
        maxRetries: Int = 3,
        requestBlock: suspend () -> HttpResponse,
    ): Result<NetworkResult<Response>> {
        var attempt = 0
        while (attempt <= maxRetries) {
            val result = runCatching { requestBlock() }

            val response = result.getOrNull()

            if (response == null) {
                logging.e(result.exceptionOrNull()) { "Request failed on attempt ${attempt + 1}" }

                // If we've reached the retry limit, fail
                if (attempt == maxRetries) {
                    return result.exceptionOrNull()?.asFailure()
                            ?: Exception("Unknown error after $maxRetries retries").asFailure()
                }

                attempt++
                continue
            }

            // Handle Unauthorized
            if (response.status.value == UnauthorizedCode) {
                logging.w { "Unauthorized, attempting token refresh (attempt ${attempt + 1})" }

                if (tryRefreshToken()) {
                    logging.d { "Token refresh successful, retrying request" }
                    attempt++ // Retry the request after refreshing the token
                    continue
                } else {
                    logging.e { "Token refresh failed, signing out user" }
                    sessionRepository.send(SessionEffect.UserIsSignedOut)
                    return UnauthorizedException("Unauthorized, user signed out").asFailure()
                }
            }

            return try {
                parseResponse<Response>(response).asSuccess()
            } catch (e: Throwable) {
                logging.e(e) { "Failed to parse response" }
                return Result.failure(e)
            }
        }

        return Exception("Unexpected error after $maxRetries retries").asFailure()
    }


    private suspend fun tryRefreshToken(): Boolean {
        logging.d { "Attempting token refresh" }
        val refreshToken = tokenDataSource.getRefreshToken() ?: return false

        val refreshResponse = runCatching {
            client.post("$BASE_URL/refresh") {
                setBody(RefreshRequest(refreshToken))
            }
        }.getOrNull()

        logging.d { "2: $refreshResponse"}

        return if (refreshResponse != null && refreshResponse.status.isSuccess()) {
            logging.d { "1: ${refreshResponse.bodyAsText()}"}
            val tokens = refreshResponse.body<AuthResponse>()
            tokenDataSource.saveAccessToken(tokens.accessToken)
            tokenDataSource.saveRefreshToken(tokens.refreshToken)
            true
        } else {
            logging.e { "Token refresh failed" }
            false
        }
    }
}

private suspend inline fun <reified Response> parseResponse(
    response: HttpResponse,
): NetworkResult<Response> = runCatching {
    NetworkResult.Success(
        status = response.status.value,
        body = response.body<Response>(),
        isResponseSuccessful = response.status.isSuccess(),
    )
}.getOrElse { throwable ->
    logging.e(throwable) { "Failed to parse response" }
    throw ResponseParsingException("Failed to parse response: ${throwable.message}")
}

private fun <T> T.asSuccess(): Result<T> = Result.success(this)
private fun <T> Throwable.asFailure(): Result<T> = Result.failure(this)

sealed interface NetworkResult<out T> {
    data class Success<out T>(
        val status: Int,
        val body: T,
        val isResponseSuccessful: Boolean,
    ): NetworkResult<T>

    data class Error(val cause: Throwable): NetworkResult<Nothing>

    fun getOrThrow(): Success<T> = when(this) {
        is Success -> this
        is Error -> throw NetworkClientException(cause)
    }
}

private val logging = logging("NetworkClient")

internal class NetworkClientException(cause: Throwable): Exception(cause)
internal class UnauthorizedException(message: String) : Exception(message)
internal class ResponseParsingException(message: String) : Exception(message)
