package com.evandhardspace.yacca.utils

import kotlin.math.pow
import kotlin.math.roundToInt

internal fun Double.formatToNDecimalPlaces(n: Int): String {
    val multiplier = 10.0.pow(n)
    val rounded = (this * multiplier).roundToInt() / multiplier
    return rounded.toString()
}