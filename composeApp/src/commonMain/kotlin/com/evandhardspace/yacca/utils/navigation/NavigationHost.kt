package com.evandhardspace.yacca.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.evandhardspace.yacca.Screen
import com.evandhardspace.yacca.presentation.favourites.FavouritesRoute
import com.evandhardspace.yacca.presentation.home.HomeRoute

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    showSnackbar: (message: String) -> Unit,
) = NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = Screen.Home.route,
) {
    composable<Route.Home> {
        HomeRoute(
            noDisabledLikeClick = showSnackbar,
        )
    }
    composable<Route.Favourites> {
        FavouritesRoute(
            onLoggedOut = {
                navController.navigateToHomeTab()
                showSnackbar("You are logged out")
            },
        )
    }
}

private fun NavController.navigateToHomeTab() = navigate(Route.Home) {
    popUpTo(graph.startDestinationId) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
