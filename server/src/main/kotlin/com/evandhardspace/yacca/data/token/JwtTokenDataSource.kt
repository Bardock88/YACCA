package com.evandhardspace.yacca.data.token

import com.evandhardspace.yacca.db.RefreshTokens
import com.evandhardspace.yacca.security.token.RefreshToken
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal interface JwtTokenDataSource {
    fun storeRefreshToken(userId: String, refreshToken: RefreshToken)

    /**
     * returns same token if it exists
     */
    fun getToken(token: String): String?
    fun deleteToken(token: String)
}

class DatabaseJwtTokenDataSource : JwtTokenDataSource {
    override fun storeRefreshToken(userId: String, refreshToken: RefreshToken) {
        transaction {
            RefreshTokens.insert {
                it[id] = refreshToken.id
                it[this.userId] = UUID.fromString(userId)
                it[token] = refreshToken.token
                it[expiresAt] = refreshToken.expiresAt
                it[createdAt] = refreshToken.createdAt
            }
        }
    }

    override fun getToken(token: String): String? =
        transaction {
            RefreshTokens
                .select { RefreshTokens.token eq token }
                .singleOrNull()
                ?.get(RefreshTokens.token)
        }


    override fun deleteToken(token: String) {
        transaction {
            RefreshTokens.deleteWhere { this.token eq token }
        }
    }
}

class InMemoryJwtTokenDataSource : JwtTokenDataSource {
    private val refreshTokens = ConcurrentHashMap<String, RefreshToken>() // Keyed by token

    override fun storeRefreshToken(userId: String, refreshToken: RefreshToken) {
        refreshTokens[refreshToken.token] = refreshToken
    }

    override fun getToken(token: String): String? {
        return refreshTokens[token]?.token
    }

    override fun deleteToken(token: String) {
        refreshTokens.remove(token)
    }
}
