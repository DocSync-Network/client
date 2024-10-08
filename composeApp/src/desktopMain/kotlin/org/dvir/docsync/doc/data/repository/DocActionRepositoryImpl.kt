package org.dvir.docsync.doc.data.repository

import org.dvir.docsync.doc.constants.DocConstants
import org.dvir.docsync.doc.data.data_source.DocsDataSource
import org.dvir.docsync.doc.data.requests.DocAction
import org.dvir.docsync.doc.domain.cursor.CursorAction
import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.cursor.CursorManager
import org.dvir.docsync.doc.domain.cursor.CursorPosition
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.domain.repository.DocActionRepository
import org.dvir.docsync.doc.domain.model.Character
import org.dvir.docsync.doc.domain.utils.CursorPosition.indexToPosition
import org.dvir.docsync.doc.domain.utils.CursorPosition.positionToIndex
import java.util.concurrent.ConcurrentHashMap

class DocActionRepositoryImpl(
    private val dataSource: DocsDataSource,
    private val cursorManager: CursorManager,
    private val document: Document,
) : DocActionRepository {
    override suspend fun addCharacter(char: Character, username: String) {
        val cursorData = cursorManager.getCursors()[username] ?: return
        val (startPos, endPos) = cursorData

        if (endPos != null) {
            removeSelection(username, startPos, endPos)
            cursorManager.updatePosition(username, startPos)
        }

        document.addCharacter(startPos, char)

        cursorManager.adjustCursors(startPos, CursorAction.Add)

        val newCursorPosition = indexToPosition(document.content, positionToIndex(document.content, startPos) + 1)
        cursorManager.updatePosition(username, newCursorPosition)

        if (username == DocConstants.OWN_USERNAME) {
            dataSource.sendDocAction(
                DocAction.Add(char)
            )
        }
    }

    override suspend fun removeCharacter(username: String) {
        val cursorData = cursorManager.getCursors()[username] ?: return
        val (startPos, endPos) = cursorData

        if (endPos != null) {
            removeSelection(username, startPos, endPos)
            return
        }

        document.removeCharacter(startPos)
        cursorManager.adjustCursors(startPos, CursorAction.Remove)

        if (username == DocConstants.OWN_USERNAME) {
            dataSource.sendDocAction(
                DocAction.Remove
            )
        }
    }

    override suspend fun editCharacter(username: String, config: CharacterConfig) {
        val cursorData = cursorManager.getCursors()[username] ?: return
        val (_, endPos) = cursorData

        if (endPos == null)
            return

        document.editCharacters(config, cursorData)
        if (username == DocConstants.OWN_USERNAME) {
            dataSource.sendDocAction(
                DocAction.Edit(
                    config = config
                )
            )
        }
    }

    override suspend fun updateCursor(cursorData: CursorData, username: String) {
        if (cursorData.end == null) {
            cursorManager.updatePosition(username, cursorData.start)
        } else {
            cursorManager.updateSelection(username, cursorData.start, cursorData.end)
        }
        if (username == DocConstants.OWN_USERNAME) {
            dataSource.sendDocAction(
                DocAction.UpdateCursorData(cursorData)
            )
        }
    }

    override fun getCursors(): ConcurrentHashMap<String, CursorData> = cursorManager.getCursors()
    override fun getConfig(cursorPosition: CursorPosition): CharacterConfig {
        val character = document.content[positionToIndex(document.content, cursorPosition)]

        return if (character is Character.Visible) {
            character.config
        } else {
            CharacterConfig(
                isBold = false,
                isItalic = false,
                isUnderlined = false,
                color = "#FF000000",
                fontSize = 11
            )
        }
    }

    override suspend fun saveDocument() {
        dataSource.sendDocAction(DocAction.Save)
    }



    override suspend fun closeDocument() {
        dataSource.leaveDoc()
    }

    override suspend fun addAccess(username: String) {
        dataSource.sendDocAction(
            DocAction.AddAccess(username)
        )
    }

    private fun removeSelection(username: String, startPos: CursorPosition, endPos: CursorPosition) {
        val startIndex = positionToIndex(document.content, startPos)
        val endIndex = positionToIndex(document.content, endPos)

        if (startIndex > endIndex) {
            throw IllegalArgumentException("Start position must be before end position")
        }

        document.content.subList(startIndex, endIndex).clear()

        for (i in startIndex until endIndex) {
            val position = indexToPosition(document.content, i)
            cursorManager.adjustCursors(position, CursorAction.Remove)
        }

        cursorManager.updatePosition(username, startPos)
    }
}
