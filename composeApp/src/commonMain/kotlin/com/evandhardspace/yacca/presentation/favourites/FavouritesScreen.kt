package com.evandhardspace.yacca.presentation.favourites

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun FavouritesRoute() {
    Text(
        modifier = Modifier.statusBarsPadding(),
        text = "Favourite Screen",
    )
}
