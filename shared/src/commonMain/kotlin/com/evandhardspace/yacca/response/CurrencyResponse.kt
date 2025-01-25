package com.evandhardspace.yacca.response

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponse(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
)
