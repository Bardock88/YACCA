package com.evandhardspace.yacca

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.presentation.SessionEffect
import com.evandhardspace.yacca.presentation.SessionRepository
import com.evandhardspace.yacca.utils.OnEffect
import com.evandhardspace.yacca.utils.di.YaccaApplication
import com.evandhardspace.yacca.utils.navigation.NavigationHost
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import org.koin.compose.koinInject


@Composable
fun App() = YaccaApplication {
    val sessionRepository = koinInject<SessionRepository>()
    val cleanUpManager = koinInject<CleanUpManager>()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var snackbarJob: Job? = remember { null }

    OnEffect(sessionRepository.effect) { effect ->
        when (effect) {
            SessionEffect.UserIsSignedOut -> {
                cleanUpManager.clear()
                snackbarHostState.showSnackbar("Your credential expired, please sign in again.")
            }
        }
    }
    MaterialTheme {

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPaddings ->
            NavigationHost(
                modifier = Modifier
                    .consumeWindowInsets(WindowInsets.systemBars)
                    .padding(innerPaddings),
                navController = navController,
                showSnackbar = { message ->
                    snackbarJob?.cancel()
                    snackbarJob = coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
    }
}
