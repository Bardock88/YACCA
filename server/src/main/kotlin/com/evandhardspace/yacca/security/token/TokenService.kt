package com.evandhardspace.yacca.security.token

interface TokenService {
    fun generate(vararg claims: TokenClaim): String
}
