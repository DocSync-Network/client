package org.dvir.docsync.auth.presentation.signup.viewmodel

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

class SignupViewModel(
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

    fun onEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.ChangedPasswordFocus ->  passwordTextField.value = passwordTextField.value.copy(
                isHintVisible = !event.focusState.isFocused && passwordTextField.value.text.isBlank()
            )
            is SignupEvent.ChangedUsernameFocus -> usernameTextField.value = usernameTextField.value.copy(
                isHintVisible = !event.focusState.isFocused
                        && usernameTextField.value.text.isBlank()
            )
            is SignupEvent.EnteredPassword -> passwordTextField.value = passwordTextField.value.copy(
                text = event.content
            )
            is SignupEvent.EnteredUsername -> usernameTextField.value = usernameTextField.value.copy(
                text = event.content
            )
            SignupEvent.OnNavigateToSignup -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UiEvent.Navigate(to = Route.Login)
                    )
                }
            }
            SignupEvent.OnSignup -> {
                viewModelScope.launch {
                    isLoading.value = true
                    val result = authRepository.signup(
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