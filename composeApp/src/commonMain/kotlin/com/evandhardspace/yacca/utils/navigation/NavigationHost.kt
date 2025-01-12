package com.evandhardspace.yacca.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.evandhardspace.yacca.Screen
import com.evandhardspace.yacca.presentation.favourites.FavouritesScreen
import com.evandhardspace.yacca.presentation.home.HomeScreen

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) = NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = Screen.Home.route,
) {
    composable<Route.Home> { HomeScreen() }
    composable<Route.Favourites> { FavouritesScreen() }
}
