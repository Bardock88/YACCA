package com.evandhardspace.yacca.security.token

import com.auth0.jwt.interfaces.DecodedJWT
import java.util.UUID

interface TokenService {
    fun generateAccessToken(vararg claims: TokenClaim): String
    fun verifyAccessToken(token: String): DecodedJWT?
    fun generateRefreshToken(userId: UUID): RefreshToken
    fun verifyRefreshToken(token: String): DecodedJWT?
    fun storeRefreshToken(userId: String, refreshToken: RefreshToken)
    fun rotateRefreshToken(oldToken: String): RefreshToken?
}
