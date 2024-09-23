package org.dvir.docsync.auth.presentation.login.viewmodel

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
import org.dvir.docsync.core.ui.TextFieldState

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    var usernameTextField = mutableStateOf(TextFieldState())
        private set

    var passwordTextField = mutableStateOf(TextFieldState())
        private set

    var isLoading = mutableStateOf(false)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.ChangedPasswordFocus ->  passwordTextField.value = passwordTextField.value.copy(
                isHintVisible = !event.focusState.isFocused && passwordTextField.value.text.isBlank()
            )
            is LoginEvent.ChangedUsernameFocus -> usernameTextField.value = usernameTextField.value.copy(
                isHintVisible = !event.focusState.isFocused
                        && usernameTextField.value.text.isBlank()
            )
            is LoginEvent.EnteredPassword -> passwordTextField.value = passwordTextField.value.copy(
                text = event.content
            )
            is LoginEvent.EnteredUsername -> usernameTextField.value = usernameTextField.value.copy(
                text = event.content
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
                    isLoading.value = true
                    val result = authRepository.login(
                        username = usernameTextField.value.text,
                        password = passwordTextField.value.text
                    )
                    isLoading.value = false

                    if (result is Result.Success) {
                        _uiEvent.send(UiEvent.Success)
                    } else {
                        _uiEvent.send(UiEvent.Error(result.message ?: "Unknown error"))
                    }
                }
            }
        }
    }
}