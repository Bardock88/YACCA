package com.evandhardspace.yacca.utils.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Favourites : Route
}
