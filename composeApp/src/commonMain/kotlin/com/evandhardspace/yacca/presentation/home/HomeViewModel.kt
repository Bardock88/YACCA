package com.evandhardspace.yacca.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.utils.formatToNDecimalPlaces
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val currencyRepository: CurrencyRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        HomeScreenState(
            currencyState = CurrencyState.Loading,
            isUserLogged = false,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch { updateAllCurrencies() }

        userRepository
            .isUserLoggedIn()
            .onEach { isUserLogged ->
                _viewState.update { it.copy(isUserLogged = isUserLogged) }
            }.launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(currencyState = CurrencyState.Loading)
            }
            updateAllCurrencies()
        }
    }

    fun addToFavourite(currencyId: String) {
        viewModelScope.launch {
            currencyRepository.addToFavourites(currencyId)
                .fold(
                    onSuccess = {
                        // start
                        _viewState.update { state ->
                            val currencyState = state.currencyState
                            if(currencyState !is CurrencyState.CurrencyLoaded) return@update state
                            state.copy(
                                currencyState = currencyState.copy(currencies = currencyState.currencies.map {
                                    if(it.id == currencyId) it.copy(isFavourite = true)
                                    else it
                                })
                            )
                        } // todo remove
                        // end
                    },
                    onFailure = { /* todo */}
                )
        }
    }

    private suspend fun updateAllCurrencies() {
        _viewState.update { state ->
            currencyRepository.allCurrencies()
                .map { currencies ->
                    currencies.map {
                        CurrencyUi(
                            id = it.id,
                            name = it.name,
                            symbol = it.symbol,
                            price = "$${it.priceUsd.formatToNDecimalPlaces(3) }",
                            isFavourite = it.isFavourite,
                        )
                    }
                }
                .fold(
                    onSuccess = { currencies ->
                        state.copy(
                            currencyState = CurrencyState.CurrencyLoaded(currencies)
                        )
                    },
                    onFailure = {
                        state.copy(currencyState = CurrencyState.Error)
                    }
                )
        }
    }
}