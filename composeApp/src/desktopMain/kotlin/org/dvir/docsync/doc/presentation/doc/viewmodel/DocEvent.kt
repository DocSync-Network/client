package org.dvir.docsync.doc.presentation.doc.viewmodel

import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.cursor.CursorPosition
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Character

sealed class DocEvent {
    data class AddCharacter(val char: Character) : DocEvent()
    data class RemoveCharacter(val position: CursorPosition) : DocEvent()
    data class EditCharacter(val config: CharacterConfig) : DocEvent()
    data class UpdateCursor(val cursorData: CursorData) : DocEvent()
    data class SaveDocument(val documentId: String) : DocEvent()
    data object CloseDocument : DocEvent()
}
