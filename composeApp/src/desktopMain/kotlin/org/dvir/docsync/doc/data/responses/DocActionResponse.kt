package org.dvir.docsync.doc.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.dvir.docsync.doc.domain.model.Character
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.cursor.CursorData

@Serializable
sealed class DocActionResponse {
    @Serializable
    @SerialName("added")
    data class Added(val username: String, val char: Character) : DocActionResponse()
    @Serializable
    @SerialName("removed")
    data class Remove(val username: String) : DocActionResponse()
    @Serializable
    @SerialName("edited")
    data class Edited(val username: String, val config: CharacterConfig) : DocActionResponse()
    @Serializable
    @SerialName("updatedCursor")
    data class UpdatedCursorData(val username: String, val data: CursorData) : DocActionResponse()
    @Serializable
    @SerialName("accessRemoved")
    data object AccessRemoved : DocActionResponse()
}