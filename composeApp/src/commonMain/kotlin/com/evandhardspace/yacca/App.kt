package com.evandhardspace.yacca

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.evandhardspace.yacca.effecthandler.SessionEffectHandler
import com.evandhardspace.yacca.effecthandler.SnackbarHandler
import com.evandhardspace.yacca.presentation.SnackbarState
import com.evandhardspace.yacca.utils.di.YaccaApplication
import com.evandhardspace.yacca.utils.navigation.NavigationHost

@Composable
fun App() = YaccaApplication {
    val navController = rememberNavController()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    var snackbarState: SnackbarState by remember { mutableStateOf(SnackbarState.General) }

    SessionEffectHandler()
    SnackbarHandler(snackbarHostState) { newState -> snackbarState = newState }

    MaterialTheme {

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                ) { data ->
                    AppSnackbar(
                        data,
                        snackbarState,
                    )
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPaddings ->
            NavigationHost(
                modifier = Modifier
                    .consumeWindowInsets(WindowInsets.systemBars)
                    .padding(innerPaddings),
                navController = navController,
            )
        }
    }
}

@Composable
internal fun AppSnackbar(
    data: SnackbarData,
    state: SnackbarState,
    modifier: Modifier = Modifier,
) {
    val (containerColor, contentColor) = when (state) {
        SnackbarState.Error -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onBackground
        SnackbarState.General -> SnackbarDefaults.color to SnackbarDefaults.contentColor
        SnackbarState.Success -> SnackbarDefaults.color to SnackbarDefaults.contentColor
    }
    Snackbar(
        modifier = modifier,
        snackbarData = data,
        containerColor = containerColor,
        contentColor = contentColor,
    )
}
