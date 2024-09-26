package org.dvir.docsync.core.navigation

import org.dvir.docsync.doc.domain.model.Document

sealed class Route() {
    // auth
    data object Splash : Route()
    data object Login : Route()
    data object Signup : Route()

    // docs
    data object Home : Route()
    data class Doc(val doc: Document) : Route()
}