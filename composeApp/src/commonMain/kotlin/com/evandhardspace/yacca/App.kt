package com.evandhardspace.yacca

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.evandhardspace.yacca.utils.di.YaccaApplication
import com.evandhardspace.yacca.utils.navigation.NavigationHost

@Composable
fun App() = YaccaApplication {
    MaterialTheme {
        val navController = rememberNavController()
        Scaffold(
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
