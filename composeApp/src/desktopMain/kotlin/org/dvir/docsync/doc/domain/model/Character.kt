package org.dvir.docsync.doc.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Character {
    @Serializable
    @SerialName("BreakLine")
    data object BreakLine : Character()

    @Serializable
    @SerialName("Space")
    data object Space : Character()

    @Serializable
    @SerialName("Visible")
    data class Visible(
        val char: Char,
        val config: CharacterConfig
    ) : Character()
}