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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.evandhardspace.yacca.presentation.navigation.NavigationViewModel
import com.evandhardspace.yacca.utils.navigation.Route
import com.evandharpace.yacca.Res
import com.evandharpace.yacca.favourites
import com.evandharpace.yacca.home
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

sealed class Screen(val route: Route, val title: StringResource, val icon: ImageVector) {
    data object Home : Screen(Route.Home, Res.string.home, Icons.Default.Home)
    data object Favourites : Screen(Route.Favourites, Res.string.favourites, Icons.Default.Favorite)

    companion object {
        fun screens() = listOf(Home, Favourites)
    }
}

@Composable
internal fun BottomNavigationBar(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by navigationViewModel.navigationState.collectAsStateWithLifecycle()

    NavigationBar(modifier = modifier) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination
        Screen.screens().forEach { screen ->
            NavigationBarItem(
                modifier = Modifier.navigationBarsPadding(),
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.title)) },
                enabled = if(screen is Screen.Favourites) state.isFavouriteEnabled else true,
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
