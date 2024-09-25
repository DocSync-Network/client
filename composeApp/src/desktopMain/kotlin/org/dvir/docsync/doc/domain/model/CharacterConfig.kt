package org.dvir.docsync.doc.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterConfig(
    val isBold: Boolean,
    val isItalic: Boolean,
    val isUnderlined: Boolean,
    val color: String,
    val fontSize: Int
)
