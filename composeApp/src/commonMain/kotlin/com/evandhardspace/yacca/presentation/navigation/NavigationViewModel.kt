package com.evandhardspace.yacca.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class NavigationViewModel(
    userRepository: UserRepository,
) : ViewModel() {

    init {
        userRepository.isUserLoggedIn()
            .onEach { isLoggedIn ->
                _navigationState.update {
                    it.copy(isFavouriteEnabled = isLoggedIn)
                }
            }
            .launchIn(viewModelScope)
    }

    private val _navigationState = MutableStateFlow(
        NavigationState(
            isFavouriteEnabled = false,
        ),
    )
    val navigationState = _navigationState.asStateFlow()

}

data class NavigationState(
    val isFavouriteEnabled: Boolean,
)