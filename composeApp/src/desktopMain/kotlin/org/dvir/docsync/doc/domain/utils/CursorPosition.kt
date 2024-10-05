package org.dvir.docsync.doc.domain.utils

import org.dvir.docsync.doc.domain.cursor.CursorPosition
import org.dvir.docsync.doc.domain.model.Character

object CursorPosition {
    fun indexToPosition(content: List<Character>, index: Int): CursorPosition {
        var line = 0
        var column = 0
        for (i in 0 until index) {
            if (i >= content.size) break
            when (content[i]) {
                is Character.BreakLine -> {
                    line++
                    column = 0
                }
                else -> column++
            }
        }
        return CursorPosition(line, column)
    }

    fun positionToIndex(content: List<Character>, position: CursorPosition): Int {
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