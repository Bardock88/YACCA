package com.evandhardspace.yacca

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.evandhardspace.yacca.navigation.Route

sealed class Screen(val route: Route, val title: String, val icon: ImageVector) {
    data object Home : Screen(Route.Home, "Home", Icons.Default.Home)
    data object Favourites : Screen(Route.Favourites, "Favourites", Icons.Default.Favorite)
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val items = listOf(Screen.Home, Screen.Favourites)
    NavigationBar(modifier = modifier) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination
        items.forEach { screen ->
            NavigationBarItem(
                modifier = Modifier.navigationBarsPadding(),
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentRoute?.hasRoute(screen.route::class) ?: false,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
