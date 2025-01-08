package com.evandhardspace.yacca

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.evandhardspace.yacca.di.appModule
import com.evandhardspace.yacca.navigation.NavigationHost
import org.koin.compose.KoinApplication

@Composable
fun App() = KoinApplication(application = { modules(appModule) }) {
    MaterialTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPaddings ->
            NavigationHost(
                modifier = Modifier.padding(innerPaddings),
                navController = navController,
            )
        }
    }
}
