package org.dvir.docsync.auth.presentation.login

import org.dvir.docsync.core.ui.TextFieldState

data class LoginState(
    val usernameTextField: TextFieldState = TextFieldState(),
    val passwordTextField: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false
)
