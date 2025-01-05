package com.evandhardspace.yacca.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String,
)
