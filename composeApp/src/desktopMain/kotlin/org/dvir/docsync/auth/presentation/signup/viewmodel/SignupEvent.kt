package org.dvir.docsync.auth.presentation.signup.viewmodel

import androidx.compose.ui.focus.FocusState

sealed interface SignupEvent {
    data object OnSignup: SignupEvent
    data object OnNavigateToSignup: SignupEvent
    data class EnteredUsername(val content: String): SignupEvent
    data class ChangedUsernameFocus(val focusState: FocusState): SignupEvent
    data class EnteredPassword(val content: String): SignupEvent
    data class ChangedPasswordFocus(val focusState: FocusState): SignupEvent
}