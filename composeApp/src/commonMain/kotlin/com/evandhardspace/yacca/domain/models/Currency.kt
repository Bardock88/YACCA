package com.evandhardspace.yacca.domain.models

data class Currency(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
    val isFavourite: Boolean,
)