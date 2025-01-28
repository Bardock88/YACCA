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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evandhardspace.yacca.presentation.errorSnackbar
import com.evandhardspace.yacca.presentation.generalSnackbar
import com.evandhardspace.yacca.presentation.home.CurrencyState
import com.evandhardspace.yacca.presentation.successSnackbar
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
            FavouriteCurrenciesEffect.LoggedOut -> {
                viewModel.sendSnackbar(generalSnackbar("You are logged out."))
                onLoggedOut()
            }

            FavouriteCurrenciesEffect.UnableToDelete -> viewModel.sendSnackbar(errorSnackbar("Unable to delete currency from favourite"))
            FavouriteCurrenciesEffect.UnableToUpdate -> viewModel.sendSnackbar(errorSnackbar("Unable to update favourite currencies"))
            is FavouriteCurrenciesEffect.NetworkStateChanged -> viewModel.sendSnackbar(
                if (effect.isNetworkAvailable) successSnackbar("Network connection is available")
                else errorSnackbar("No network connection")
            )
        }
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
                    onLikeClick = viewModel::deleteFromFavourites,
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
    onLikeClick: (currencyId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (currencyState.currencies.isEmpty()) {
        Box(modifier) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "You have no favourite currencies",
            )
        }
        return
    }
    LazyColumn(modifier.fillMaxSize()) {
        items(currencyState.currencies, key = { it.id }) { currency ->
            CurrencyCard(
                currency = currency,
                isLikeEnabled = true,
                onLikeClick = { onLikeClick(it.id) },
                onDisabledLikeClick = { /* no-op */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
        }
    }
}
