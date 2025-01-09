package com.evandhardspace.yacca.domain

data class Currency(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
)