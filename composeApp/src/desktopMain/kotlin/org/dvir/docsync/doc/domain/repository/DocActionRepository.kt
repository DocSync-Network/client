package org.dvir.docsync.doc.domain.repository

import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.cursor.CursorPosition
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Character
import java.util.concurrent.ConcurrentHashMap

interface DocActionRepository {
    suspend fun addCharacter(char: Character, username: String)
    suspend fun removeCharacter(username: String)
    suspend fun editCharacter(username: String, config: CharacterConfig)
    suspend fun updateCursor(cursorData: CursorData, username: String)

    suspend fun addAccess(username: String)
    fun getCursors(): ConcurrentHashMap<String, CursorData>
    fun getConfig(cursorPosition: CursorPosition): CharacterConfig
    suspend fun saveDocument()
    suspend fun closeDocument()
}
