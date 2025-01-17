package com.evandhardspace.yacca.utils.client

import com.evandhardspace.yacca.data.datasources.TokenDataSource
import com.evandhardspace.yacca.data.network.auth
import com.evandhardspace.yacca.presentation.SessionEffect
import com.evandhardspace.yacca.presentation.SessionRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

private const val UnauthorizedCode = 401

internal class NetworkClient(
    private val client: HttpClient,
    private val tokenDataSource: TokenDataSource,
    private val sessionRepository: SessionRepository,
) {

    suspend inline fun <reified Response> get(
        urlString: String,
        withAuth: Boolean = false,
    ): NetworkResult<Response> {
        val result: NetworkResult<Response> = getRequest(urlString, withAuth)
        if (result.status == UnauthorizedCode) sessionRepository.send(SessionEffect.UserIsSignedOut)
        return result
    }

    suspend inline fun <reified Request, reified Response> post(
        urlString: String,
        body: Request? = null,
        withAuth: Boolean = false,
    ): NetworkResult<Response> {
        val result: NetworkResult<Response> = postRequest(urlString, body, withAuth)
        if (result.status == UnauthorizedCode) sessionRepository.send(SessionEffect.UserIsSignedOut)
        return result
    }

    suspend inline fun <reified Response> delete(
        urlString: String,
        withAuth: Boolean = false,
    ): NetworkResult<Response> {
        val result: NetworkResult<Response> = deleteRequest(urlString, withAuth)
        if (result.status == UnauthorizedCode) sessionRepository.send(SessionEffect.UserIsSignedOut)
        return result
    }

    private suspend inline fun <reified Response> getRequest(
        urlString: String,
        withAuth: Boolean = false,
    ): NetworkResult<Response> {
        val result = client.get(urlString) {
            if (withAuth) auth(tokenDataSource)
        }
        return NetworkResult(
            status = result.status.value,
            body = result.body<Response>(),
            isSuccess = result.status.isSuccess(),
        )
    }

    private suspend inline fun <reified Request, reified Response> postRequest(
        urlString: String,
        body: Request? = null,
        withAuth: Boolean = false,
    ): NetworkResult<Response> {
        val result = client.post(urlString) {
            body?.let { setBody(body) }
            if (withAuth) auth(tokenDataSource)
        }
        return NetworkResult(
            status = result.status.value,
            body = result.body<Response>(),
            isSuccess = result.status.isSuccess(),
        )
    }

    private suspend inline fun <reified Response> deleteRequest(
        urlString: String,
        withAuth: Boolean = false,
    ): NetworkResult<Response> {
        val result = client.delete(urlString) {
            if (withAuth) auth(tokenDataSource)
        }
        return NetworkResult(
            status = result.status.value,
            body = result.body<Response>(),
            isSuccess = result.status.isSuccess(),
        )
    }
}

data class NetworkResult<T>(
    val status: Int,
    val body: T,
    val isSuccess: Boolean,
)