package com.evandhardspace.yacca.presentation.home

internal data class CurrencyUi(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
    val isFavourite: Boolean,
)
