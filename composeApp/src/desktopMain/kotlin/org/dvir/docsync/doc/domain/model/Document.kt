package org.dvir.docsync.doc.domain.model

import kotlinx.serialization.Serializable
import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.cursor.CursorPosition
import org.dvir.docsync.doc.domain.utils.CursorPosition.positionToIndex

typealias ID = String

@Serializable
data class Document(
    val id: ID,
    val owner: String,
    val name: String,
    val creationDate: Long,
    val access: MutableList<String>,
    val content: MutableList<Character>,
) {
    fun editCharacters(config: CharacterConfig, cursorData: CursorData) {
        if (cursorData.end == null) return

        val start = positionToIndex(content, cursorData.start)
        val end = positionToIndex(content, cursorData.end)

        val sublist = content.subList(start, end)

        for (i in sublist.indices) {
            val character = sublist[i]
            if (character is Character.Visible) {
                sublist[i] = character.copy(
                    config = config
                )
            }
        }
    }

    fun addCharacter(position: CursorPosition, character: Character) {
        val index = positionToIndex(content, position)
        content.add(index, character)
    }


    fun removeCharacter(position: CursorPosition) {
        val index = positionToIndex(content, position)
        content.removeAt(index)
    }
}