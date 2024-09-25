package org.dvir.docsync.doc.domain.model

import kotlinx.serialization.Serializable
import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.cursor.CursorPosition

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
    fun editCharacters(characterConfig: CharacterConfig, cursorData: CursorData) {
        if (cursorData.end == null) return

        val start = positionToIndex(cursorData.start)
        val end = positionToIndex(cursorData.end)

        val sublist = content.subList(start, end)

        for (i in sublist.indices) {
            val character = sublist[i]
            if (character is Character.Visible) {
                sublist[i] = character.copy(
                    config = CharacterConfig(
                        isBold = characterConfig.isBold,
                        isItalic = characterConfig.isItalic,
                        isUnderlined = characterConfig.isUnderlined,
                        color = characterConfig.color,
                        fontSize = characterConfig.fontSize
                    )
                )
            }
        }
    }

    fun addCharacter(position: CursorPosition, character: Character) {
        val index = positionToIndex(position)
        content.add(index, character)
    }


    fun removeCharacter(position: CursorPosition) {
        val index = positionToIndex(position)
        content.removeAt(index)
    }

    private fun positionToIndex(position: CursorPosition): Int {
        var index = 0
        var line = 0
        var column = 0
        while (line < position.line || (line == position.line && column < position.column)) {
            if (index >= content.size) {
                break
            }
            when (content[index]) {
                is Character.BreakLine -> {
                    line++
                    column = 0
                }
                else -> column++
            }
            index++
        }
        return index
    }
}