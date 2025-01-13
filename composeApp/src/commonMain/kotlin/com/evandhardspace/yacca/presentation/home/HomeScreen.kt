package com.evandhardspace.yacca.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evandhardspace.yacca.presentation.login.LoginScreen
import com.evandhardspace.yacca.ui.CurrencyCard
import com.evandhardspace.yacca.utils.pxAsDp
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeRoute(
    noDisabledLikeClick: (message: String) -> Unit,
) {
    HomeScreen(
        onDisabledLikeClick = { noDisabledLikeClick("Login to add favourite currencies") }
    ) { onDismiss ->
        LoginScreen(
            modifier = Modifier.fillMaxWidth(),
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onDisabledLikeClick: () -> Unit,
    authBottomSheetContent: @Composable (onDismiss: () -> Unit) -> Unit,
) {
    val uiState by viewModel.viewState.collectAsStateWithLifecycle()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAuthSheet by remember { mutableStateOf(false) }

    if (showAuthSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAuthSheet = false },
            sheetState = bottomSheetState,
        ) {
            authBottomSheetContent { showAuthSheet = false }
        }
    }
    var authSectionHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val currencyState = uiState.currencyState) {
                is CurrencyState.CurrencyLoaded -> CurrencyContent(
                    currencyState = currencyState,
                    isUserLogged = uiState.isUserLogged,
                    onLikeClick = { currency -> /* TODO */ },
                    onDisabledLikeClick = onDisabledLikeClick,
                    topContent = (@Composable { Spacer(Modifier.height(authSectionHeight.pxAsDp + 8.dp)) })
                        .takeUnless { uiState.isUserLogged },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
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
        AnimatedVisibility(
            visible = uiState.isUserLogged.not(),
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight } // Slide in from the top
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight } // Slide out upwards
            ) + fadeOut(),
        ) {
            AuthSection(
                onShowAuth = { showAuthSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .onSizeChanged { authSectionHeight = it.height }
            )
        }
    }
}

@Composable
private fun CurrencyContent(
    currencyState: CurrencyState.CurrencyLoaded,
    isUserLogged: Boolean,
    onLikeClick: (CurrencyUi) -> Unit,
    onDisabledLikeClick: () -> Unit,
    topContent: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {
        topContent?.let { content ->
            item { content() }
        }
        items(currencyState.currencies, key = { it.id }) { currency ->
            CurrencyCard(
                currency = currency,
                isLikeEnabled = isUserLogged,
                onLikeClick = onLikeClick,
                onDisabledLikeClick = onDisabledLikeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun AuthSection(
    onShowAuth: () -> Unit,
    modifier: Modifier = Modifier,
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Text message
            Text(
                text = "Please sign in to access more features.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f) // Take available space before the button
            )

            // Button with flexible width
            Button(
                onClick = onShowAuth,
                modifier = Modifier.padding(start = 8.dp) // Add spacing from the text
            ) {
                Text(
                    text = "Sign in",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1, // Prevent infinite line wrapping
                    overflow = TextOverflow.Ellipsis // Handle any overflow
                )
            }
        }
    }
}
