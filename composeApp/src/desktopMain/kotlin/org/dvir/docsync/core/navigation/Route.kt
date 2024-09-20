package org.dvir.docsync.core.navigation

sealed class Route(val name: String) {
    // auth
    data object Splash : Route(name = "splash")
    data object Login : Route(name = "login")
    data object Signup : Route(name = "signup")
}