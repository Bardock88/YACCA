package com.evandhardspace.yacca.data.response

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponse(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
)
