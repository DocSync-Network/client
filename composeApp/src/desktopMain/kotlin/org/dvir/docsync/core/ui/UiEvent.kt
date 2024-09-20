package org.dvir.docsync.core.ui

import org.dvir.docsync.core.navigation.Route

sealed interface UiEvent {
    data class Navigate(val to: Route) : UiEvent
    data class Error(val message: String) : UiEvent
    data object Success : UiEvent
}