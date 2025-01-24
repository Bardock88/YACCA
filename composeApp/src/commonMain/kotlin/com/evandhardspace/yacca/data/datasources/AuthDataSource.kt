package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.Endpoints
import com.evandhardspace.yacca.request.AuthRequest
import com.evandhardspace.yacca.response.AuthResponse
import com.evandhardspace.yacca.utils.client.NetworkClient

internal interface AuthDataSource {
    suspend fun signUp(email: String, password: String)
    suspend fun signIn(email: String, password: String): AuthResponse
}

internal class NetworkAuthDataSource(
    private val networkClient: NetworkClient,
) : AuthDataSource {
    override suspend fun signUp(email: String, password: String) {
        networkClient.post<AuthRequest, Unit>(
            "$BASE_URL/${Endpoints.SIGNUP}",
            body = AuthRequest(
                email = email,
                password = password,
            )
        )
    }

    override suspend fun signIn(email: String, password: String): AuthResponse =
        networkClient.post<AuthRequest, AuthResponse>(
            "$BASE_URL/${Endpoints.SIGNIN}",
            body = AuthRequest(
                email = email,
                password = password,
            )
        ).getOrThrow().body
}