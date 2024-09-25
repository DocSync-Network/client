package org.dvir.docsync.doc.domain.cursor

import kotlinx.serialization.Serializable

@Serializable
data class CursorData(
    val start: CursorPosition,
    val end: CursorPosition? = null
)
