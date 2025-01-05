package com.evandhardspace.yacca

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform