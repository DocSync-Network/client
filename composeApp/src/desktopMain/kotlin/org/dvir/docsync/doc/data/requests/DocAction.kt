package org.dvir.docsync.doc.data.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.dvir.docsync.doc.domain.model.Character
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.cursor.CursorData

@Serializable
sealed class DocAction {
    @Serializable
    @SerialName("edit")
    data class Edit(val config: CharacterConfig): DocAction()
    @Serializable
    @SerialName("add")
    data class Add(val char: Character) : DocAction()
    @Serializable
    @SerialName("addAccess")
    data class AddAccess(val addedUsername: String) : DocAction()
    @Serializable
    @SerialName("removeAccess")
    data class RemoveAccess(val removedUsername: String) : DocAction()
    @Serializable
    @SerialName("cursor")
    data class UpdateCursorData(val data: CursorData) : DocAction()
    @Serializable
    @SerialName("remove")
    data object Remove : DocAction()
    @Serializable
    @SerialName("save")
    data object Save : DocAction()
}