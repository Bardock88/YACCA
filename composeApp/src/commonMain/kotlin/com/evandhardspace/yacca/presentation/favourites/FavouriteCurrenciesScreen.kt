package com.evandhardspace.yacca.presentation.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evandhardspace.yacca.presentation.home.CurrencyState
import com.evandhardspace.yacca.presentation.home.CurrencyUi
import com.evandhardspace.yacca.ui.CurrencyCard
import com.evandhardspace.yacca.utils.OnEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FavouritesRoute(
    onLoggedOut: () -> Unit,
) {
    FavouriteCurrenciesScreenScreen(
        onLoggedOut = onLoggedOut,
    )
}

@Composable
private fun FavouriteCurrenciesScreenScreen(
    viewModel: FavouriteCurrenciesViewModel = koinViewModel(),
    onLoggedOut: () -> Unit,
) {
    val uiState by viewModel.viewState.collectAsStateWithLifecycle()

    OnEffect(viewModel.effect) { effect ->
        when (effect) {
            FavouriteCurrenciesEffect.LoggedOut -> onLoggedOut()
        }
    }

    // todo: remove once depending on local data
    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel.refresh()
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = viewModel::refresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(onClick = viewModel::logout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val currencyState = uiState) {
                is CurrencyState.CurrencyLoaded -> CurrencyContent(
                    currencyState = currencyState,
                    onLikeClick = { currency -> /* TODO */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                )

                CurrencyState.Error -> Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Something went wrong.", color = MaterialTheme.colorScheme.error)
                }

                CurrencyState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun CurrencyContent(
    currencyState: CurrencyState.CurrencyLoaded,
    onLikeClick: (CurrencyUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.fillMaxSize()) {
        items(currencyState.currencies, key = { it.id }) { currency ->
            CurrencyCard(
                currency = currency,
                isLikeEnabled = true,
                onLikeClick = onLikeClick,
                onDisabledLikeClick = { /* no-op */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
        }
    }
}
