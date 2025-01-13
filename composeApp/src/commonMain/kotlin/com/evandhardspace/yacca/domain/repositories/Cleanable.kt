package com.evandhardspace.yacca.domain.repositories


interface Cleanable {
    suspend fun clear()
}
