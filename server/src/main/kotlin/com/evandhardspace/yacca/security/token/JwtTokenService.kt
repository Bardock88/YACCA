package com.evandhardspace.yacca.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.evandhardspace.yacca.data.token.JwtTokenDataSource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Date
import java.util.UUID

internal class JwtTokenService(
    private val accessTokenConfig: TokenConfig,
    private val refreshTokenConfig: TokenConfig,
    private val jwtTokenDataSource: JwtTokenDataSource,
) : TokenService {
    override fun generateAccessToken(vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(accessTokenConfig.audience)
            .withIssuer(accessTokenConfig.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenConfig.expiresIn))

        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }

        return token.sign(Algorithm.HMAC256(accessTokenConfig.secret))
    }

    override fun verifyAccessToken(token: String): DecodedJWT? = try {
        JWT.require(Algorithm.HMAC256(accessTokenConfig.secret))
            .withIssuer(accessTokenConfig.issuer)
            .build()
            .verify(token)
    } catch (e: Exception) {
        null
    }

    override fun generateRefreshToken(userId: UUID): RefreshToken {
        val expiresAt = Date(System.currentTimeMillis() + refreshTokenConfig.expiresIn)
        val token = JWT.create()
            .withAudience(refreshTokenConfig.audience)
            .withIssuer(refreshTokenConfig.issuer)
            .withClaim("userId", userId.toString()) // Add the userId as a claim
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(refreshTokenConfig.secret))

        return RefreshToken(
            id = UUID.randomUUID(),
            userId,
            token,
            expiresAt = expiresAt.toInstant().toKotlinInstant()
                .toLocalDateTime(TimeZone.currentSystemDefault()), // todo
            createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
                .toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    override fun verifyRefreshToken(token: String): DecodedJWT? = try {
        JWT.require(Algorithm.HMAC256(refreshTokenConfig.secret))
            .withIssuer(refreshTokenConfig.issuer)
            .build()
            .verify(token)
    } catch (e: Exception) {
        null
    }

    override fun storeRefreshToken(userId: String, refreshToken: RefreshToken) {
        jwtTokenDataSource.storeRefreshToken(userId, refreshToken)
    }

    override fun rotateRefreshToken(oldToken: String): RefreshToken? {
        val existingToken = jwtTokenDataSource.getToken(oldToken) ?: return null
        val decodedToken = verifyRefreshToken(existingToken) ?: return null
        val userId = decodedToken.getClaim("userId").asString() ?: return null

        jwtTokenDataSource.deleteToken(oldToken)
        val refreshToken = generateRefreshToken(UUID.fromString(userId))

        storeRefreshToken(userId, refreshToken)
        return refreshToken
    }
}
