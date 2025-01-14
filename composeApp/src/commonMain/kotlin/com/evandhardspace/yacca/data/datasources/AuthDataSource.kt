package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.request.AuthRequest
import com.evandhardspace.yacca.response.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal interface AuthDataSource {
    suspend fun signUp(email: String, password: String)
    suspend fun signIn(email: String, password: String): AuthResponse
}

internal class NetworkAuthDataSource(
    private val client: HttpClient,
) : AuthDataSource {
    override suspend fun signUp(email: String, password: String) {
        client.post("$BASE_URL/signup") {
            setBody(
                AuthRequest(
                    email = email,
                    password = password,
                )
            )
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResponse =
        client.post("$BASE_URL/signin") {
            setBody(
                AuthRequest(
                    email = email,
                    password = password,
                )
            )
        }.body()
}