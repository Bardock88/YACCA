package com.evandhardspace.yacca.data.currency

import kotlinx.serialization.Serializable

@Serializable
data class CurrenciesResource(
    val data: List<CurrencyResource>
)

@Serializable
data class CurrencyResource(
    val id: String,
    val symbol: String,
    val name: String,
    val priceUsd: Double,
)
