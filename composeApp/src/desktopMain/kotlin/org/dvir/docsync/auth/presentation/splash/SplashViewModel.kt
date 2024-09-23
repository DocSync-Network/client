package org.dvir.docsync.auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.dvir.docsync.auth.domain.repository.AuthRepository
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.result.Result
import org.dvir.docsync.core.ui.UiEvent

class SplashViewModel(
    authRepository: AuthRepository
): ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val result = authRepository.validateToken()

            if (result is Result.Success) {
                if (result.data!!) {
                    _uiEvent.send(
                        UiEvent.Success
                    )
                } else {
                    _uiEvent.send(
                        UiEvent.Navigate(
                            to = Route.Login
                        )
                    )
                }
            } else if (result is Result.Error) {
                _uiEvent.send(
                    UiEvent.Error(result.message ?: "Unknown Error")
                )
            }
        }
    }
}