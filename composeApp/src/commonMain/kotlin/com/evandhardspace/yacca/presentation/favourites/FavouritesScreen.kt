package com.evandhardspace.yacca.presentation.favourites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.evandhardspace.yacca.utils.OnEffect
import org.koin.compose.viewmodel.koinViewModel


@Composable
internal fun FavouritesRoute(
    viewModel: FavouriteCurrenciesViewModel = koinViewModel(),
    onLoggedOut: () -> Unit,
) {
    OnEffect(viewModel.effect) { effect ->
        when (effect) {
            FavouriteCurrenciesEffect.LoggedOut -> onLoggedOut()
        }
    }
    Column {
        Text("Favourite Currencies screen")
        Spacer(Modifier.height(16.dp))
        Button({ viewModel.logout() }) {
            Text("Log out")
        }
        Button({viewModel.test()}) {
            Text("Test")
        }
        Text("Currencies: ${viewModel.currenciesState}")
    }
}
