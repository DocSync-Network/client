package org.dvir.docsync.auth.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.dvir.docsync.auth.domain.repository.AuthRepository
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.UiEvent
import org.dvir.docsync.core.result.Result

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.ChangedPasswordFocus -> _state.value = _state.value.copy(
                    passwordTextField = state.value.passwordTextField.copy(
                        isHintVisible = !event.focusState.isFocused
                                && state.value.passwordTextField.text.isBlank()
                    )
                )
            is LoginEvent.ChangedUsernameFocus -> _state.value = _state.value.copy(
                usernameTextField = state.value.usernameTextField.copy(
                    isHintVisible = !event.focusState.isFocused
                            && state.value.usernameTextField.text.isBlank()
                )
            )
            is LoginEvent.EnteredPassword -> _state.value = _state.value.copy(
                passwordTextField = state.value.passwordTextField.copy(
                    text = event.content
                )
            )
            is LoginEvent.EnteredUsername -> _state.value = _state.value.copy(
                usernameTextField = state.value.usernameTextField.copy(
                    text = event.content
                )
            )
            LoginEvent.OnNavigateToSignup -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UiEvent.Navigate(to = Route.Signup)
                    )
                }
            }
            LoginEvent.OnLogin -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)
                    val result = authRepository.login(
                        username = state.value.usernameTextField.text,
                        password = state.value.passwordTextField.text
                    )
                    _state.value = state.value.copy(isLoading = false)

                    if (result is Result.Success) {
                        _uiEvent.send(UiEvent.Success)
                    } else {
                        _uiEvent.send(UiEvent.Error(result.data ?: "Unknown error"))
                    }
                }
            }
        }
    }
}