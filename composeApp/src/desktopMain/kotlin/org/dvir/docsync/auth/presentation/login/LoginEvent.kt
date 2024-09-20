package org.dvir.docsync.auth.presentation.login

import androidx.compose.ui.focus.FocusState

sealed interface LoginEvent {
    data object OnLogin: LoginEvent
    data object OnNavigateToSignup: LoginEvent
    data class EnteredUsername(val content: String): LoginEvent
    data class ChangedUsernameFocus(val focusState: FocusState): LoginEvent
    data class EnteredPassword(val content: String): LoginEvent
    data class ChangedPasswordFocus(val focusState: FocusState): LoginEvent
}