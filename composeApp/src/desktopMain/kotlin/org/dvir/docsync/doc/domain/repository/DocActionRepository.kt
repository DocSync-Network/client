package org.dvir.docsync.doc.domain.repository

import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Character

interface DocActionRepository {
    suspend fun addCharacter(char: Character, username: String)
    suspend fun removeCharacter(username: String)
    suspend fun editCharacter(username: String, config: CharacterConfig)
    suspend fun updateCursor(cursorData: CursorData, username: String)
    fun getCursors(): HashMap<String, CursorData>
    suspend fun saveDocument()
    suspend fun closeDocument()
}
