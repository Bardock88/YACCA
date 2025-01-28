package com.evandhardspace.yacca.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.evandhardspace.yacca.presentation.home.CurrencyUi
import com.evandharpace.yacca.Res
import com.evandharpace.yacca.favourite_disabled
import com.evandharpace.yacca.price
import com.evandharpace.yacca.symbol
import com.evandharpace.yacca.toggle_favourites
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CurrencyCard(
    currency: CurrencyUi,
    onLikeClick: (CurrencyUi) -> Unit,
    onDisabledLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLikeEnabled: Boolean,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${stringResource(Res.string.symbol)}: ${currency.symbol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stringResource(Res.string.price)}: ${currency.price}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (isLikeEnabled) IconButton(onClick = { onLikeClick(currency) }) {
                Icon(
                    imageVector = if (currency.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(Res.string.toggle_favourites),
                    tint = if (currency.isFavourite) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
            else IconButton(onClick = onDisabledLikeClick) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(Res.string.favourite_disabled),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}
