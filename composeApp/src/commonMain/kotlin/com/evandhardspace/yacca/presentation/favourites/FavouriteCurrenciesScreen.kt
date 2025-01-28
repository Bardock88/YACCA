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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.evandharpace.yacca.Res
import com.evandharpace.yacca.network_ko
import com.evandharpace.yacca.network_ok
import com.evandharpace.yacca.refresh
import com.evandharpace.yacca.sign_out
import com.evandharpace.yacca.unable_to_delete_from_favourites
import com.evandharpace.yacca.unable_to_update_favourite_currencies
import com.evandharpace.yacca.you_are_logged_out
import com.evandharpace.yacca.you_have_no_favourite_currencies
import org.jetbrains.compose.resources.stringResource
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
    val youAreLoggedOutString = stringResource(Res.string.you_are_logged_out)
    val unableToDeleteFromFavouritesString = stringResource(Res.string.unable_to_delete_from_favourites)
    val unableToUpdateFavouriteCurrenciesString = stringResource(Res.string.unable_to_update_favourite_currencies)
    val networkOkString = stringResource(Res.string.network_ok)
    val networkKoString = stringResource(Res.string.network_ko)

    OnEffect(viewModel.effect) { effect ->
        when (effect) {
            FavouriteCurrenciesEffect.LoggedOut -> {
                viewModel.sendSnackbar(generalSnackbar(youAreLoggedOutString))
                onLoggedOut()
            }

            FavouriteCurrenciesEffect.UnableToDelete -> viewModel.sendSnackbar(errorSnackbar(unableToDeleteFromFavouritesString))
            FavouriteCurrenciesEffect.UnableToUpdate -> viewModel.sendSnackbar(errorSnackbar(unableToUpdateFavouriteCurrenciesString))
            is FavouriteCurrenciesEffect.NetworkStateChanged -> viewModel.sendSnackbar(
                if (effect.isNetworkAvailable) successSnackbar(networkOkString)
                else errorSnackbar(networkKoString)
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = viewModel::refresh) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(Res.string.refresh))
                }
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(onClick = viewModel::logout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(Res.string.sign_out))
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
                text = stringResource(Res.string.you_have_no_favourite_currencies),
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
