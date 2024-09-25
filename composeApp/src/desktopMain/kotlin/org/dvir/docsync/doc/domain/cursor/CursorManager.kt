package org.dvir.docsync.doc.domain.cursor

import java.util.concurrent.ConcurrentHashMap

class CursorManager {
    private val cursors = ConcurrentHashMap<String, CursorData>()

    fun getCursors(): ConcurrentHashMap<String, CursorData> = cursors

    fun updatePosition(username: String, cursorPosition: CursorPosition) {
        cursors[username] = CursorData(start = cursorPosition, end = null)
    }

    fun updateSelection(username: String, start: CursorPosition, end: CursorPosition) {
        cursors[username] = CursorData(start = start, end = end)
    }

    fun adjustCursors(position: CursorPosition, action: CursorAction) {
        cursors.forEach { (username, cursorData) ->
            if (cursorData.end == null) {
                val updatedCursor = adjustPosition(cursorData.start, position, action)
                if (updatedCursor != null) {
                    cursors[username] = cursorData.copy(start = updatedCursor)
                }
            } else {
                val updatedStart = adjustPosition(cursorData.start, position, action)
                val updatedEnd = adjustPosition(cursorData.end, position, action)

                cursors[username] = CursorData(
                    start = updatedStart ?: cursorData.start,
                    end = updatedEnd ?: cursorData.end
                )
            }
        }
    }

    private fun adjustPosition(
        cursorPosition: CursorPosition,
        actionPosition: CursorPosition,
        action: CursorAction
    ): CursorPosition? {
        if (cursorPosition.line != actionPosition.line) {
            return null
        }

        return when (action) {
            CursorAction.Add -> {
                if (cursorPosition.column < actionPosition.column) {
                    null
                } else {
                    cursorPosition.copy(column = cursorPosition.column + 1)
                }
            }
            CursorAction.Remove -> {
                if (cursorPosition.column <= actionPosition.column) {
                    null
                } else {
                    cursorPosition.copy(column = cursorPosition.column - 1)
                }
            } else -> null
        }
    }
}
