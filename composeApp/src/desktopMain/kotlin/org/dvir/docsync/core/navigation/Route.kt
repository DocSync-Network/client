package org.dvir.docsync.core.navigation

sealed class Route() {
    // auth
    data object Splash : Route()
    data object Login : Route()
    data object Signup : Route()

    // docs
    data object Home : Route()
}