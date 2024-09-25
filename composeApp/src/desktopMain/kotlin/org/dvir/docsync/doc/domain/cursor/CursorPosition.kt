package org.dvir.docsync.doc.domain.cursor

import kotlinx.serialization.Serializable

@Serializable
data class CursorPosition(
    val line: Int,
    val column: Int
)
