package org.dvir.docsync.doc.presentation.doc.viewmodel

import androidx.compose.ui.graphics.Color
import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.model.Character

sealed class DocEvent {
    data class AddCharacter(val char: Character) : DocEvent()
    data class UpdateCursor(val cursorData: CursorData) : DocEvent()
    data object RemoveCharacter : DocEvent()
    data object OpenColorDialog : DocEvent()
    data object CloseColorDialog : DocEvent()
    data object OpenAccessDialog : DocEvent()
    data object CloseAccessDialog : DocEvent()
    data object SaveDocument : DocEvent()
    data object CloseDocument : DocEvent()
}

sealed class EditEvent {
    data class ChangeColor(val color: Color) : EditEvent()
    data object ToggleBold : EditEvent()
    data object ToggleItalic : EditEvent()
    data object ToggleUnderLine : EditEvent()
    data object IncreaseFontSize : EditEvent()
    data object DecreaseFontSize : EditEvent()
}
