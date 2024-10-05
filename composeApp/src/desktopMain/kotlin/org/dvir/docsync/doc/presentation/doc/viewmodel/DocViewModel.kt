package org.dvir.docsync.doc.presentation.doc.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.UiEvent
import org.dvir.docsync.doc.constants.DocConstants
import org.dvir.docsync.doc.data.responses.DocActionResponse
import org.dvir.docsync.doc.data.responses.DocResponse
import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.model.Character
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.domain.repository.DocActionRepository
import org.dvir.docsync.doc.domain.repository.DocsResponsesRepository
import org.dvir.docsync.doc.domain.utils.Colors.colorFromHex
import org.dvir.docsync.doc.domain.utils.CursorPosition.positionToIndex
import org.dvir.docsync.doc.presentation.doc.annotatedStringFromDocument

class DocViewModel(
    private val docActionRepository: DocActionRepository,
    private val docsResponsesRepository: DocsResponsesRepository,
    private val document: Document
) : ViewModel() {
    private val _textFieldValue = mutableStateOf(
        TextFieldValue(annotatedStringFromDocument(document.content))
    )
    val textFieldValue = _textFieldValue
    private val _savedSelection = mutableStateOf(TextRange(0, 0))
    val savedSelection = _savedSelection

    var isAccessDialogOpen by mutableStateOf(false)
    var isColorDialogOpen by mutableStateOf(false)
    var isBold by mutableStateOf(false)
    var isUnderlined by mutableStateOf(false)
    var isItalic by mutableStateOf(false)
    var color by mutableStateOf(Color.Black)
    var fontSize by mutableStateOf(11)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            docsResponsesRepository.responseFlow.collect { response ->
                when (response) {
                    is DocResponse.ActionResponse -> handleActionResponse(response.response)
                    is DocResponse.Error -> {
                        _uiEvent.send(
                            UiEvent.Error(message = response.errorResponse.message)
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    fun onDocEvent(event: DocEvent) {
        when (event) {
            is DocEvent.AddCharacter -> handleAdd(
                DocConstants.OWN_USERNAME,
                if (event.char is Character.Visible)
                    event.char.copy(
                        config = CharacterConfig(
                            isBold = isBold,
                            isItalic = isItalic,
                            isUnderlined = isUnderlined,
                            color = String.format("#%08X", color.toArgb()),
                            fontSize = fontSize
                        )
                    )
                else event.char
            )
            is DocEvent.RemoveCharacter -> handleRemove(DocConstants.OWN_USERNAME)
            is DocEvent.UpdateCursor -> {
                handleCursorUpdate(DocConstants.OWN_USERNAME, event.cursorData)
                if (event.cursorData.end != null) {
                    val config = docActionRepository.getConfig(event.cursorData.start)
                    isBold = config.isBold
                    isItalic = config.isItalic
                    isUnderlined = config.isUnderlined
                    fontSize = config.fontSize
                    color = colorFromHex(config.color)
                }
            }
            DocEvent.SaveDocument -> saveDocument()
            DocEvent.CloseDocument -> closeDocument()
            DocEvent.OpenColorDialog -> isColorDialogOpen = true
            DocEvent.CloseColorDialog -> isColorDialogOpen = false
            DocEvent.OpenAccessDialog -> isAccessDialogOpen = true
            DocEvent.CloseAccessDialog -> isAccessDialogOpen = false
        }
    }

    fun onEditEvent(event: EditEvent) {
        _textFieldValue.value = textFieldValue.value.copy(
            selection = savedSelection.value
        )
        when (event) {
            is EditEvent.ChangeColor -> color = event.color
            EditEvent.DecreaseFontSize -> fontSize--
            EditEvent.IncreaseFontSize -> fontSize++
            EditEvent.ToggleBold -> isBold = !isBold
            EditEvent.ToggleItalic -> isItalic = !isItalic
            EditEvent.ToggleUnderLine -> isUnderlined = !isUnderlined
        }
        viewModelScope.launch {
            docActionRepository.editCharacter(
                username = DocConstants.OWN_USERNAME,
                config = CharacterConfig(
                    isBold = isBold,
                    isUnderlined = isUnderlined,
                    isItalic = isItalic,
                    color = String.format("#%08X", color.toArgb()),
                    fontSize = fontSize
                )
            )
        }
    }

    private fun handleActionResponse(actionResponse: DocActionResponse) {
        when (actionResponse) {
            is DocActionResponse.Added -> handleAdd(actionResponse.username, actionResponse.char)
            is DocActionResponse.Remove -> handleRemove(actionResponse.username)
            is DocActionResponse.Edited -> handleEdit(actionResponse.username, actionResponse.config)
            is DocActionResponse.UpdatedCursorData -> handleCursorUpdate(actionResponse.username, actionResponse.data)
            else -> {}
        }
    }

    private fun handleAdd(username: String, character: Character) {
        viewModelScope.launch {
            docActionRepository.addCharacter(character, username)
            if (username == DocConstants.OWN_USERNAME && _savedSelection.value.start == document.content.lastIndex)
                _savedSelection.value = TextRange(document.content.size)
            _textFieldValue.value = TextFieldValue(
                annotatedString = annotatedStringFromDocument(document.content),
                selection = _savedSelection.value
            )
        }
    }

    private fun handleRemove(username: String) {
        viewModelScope.launch {
            docActionRepository.removeCharacter(username)
            _textFieldValue.value = TextFieldValue(
                annotatedString = annotatedStringFromDocument(document.content),
                selection = _savedSelection.value
            )
        }
    }

    private fun handleEdit(username: String, config: CharacterConfig) {
        viewModelScope.launch {
            docActionRepository.editCharacter(username, config)
            _textFieldValue.value = TextFieldValue(
                annotatedString = annotatedStringFromDocument(document.content),
                selection = _savedSelection.value
            )
        }
    }

    private fun handleCursorUpdate(username: String, data: CursorData) {
        viewModelScope.launch {
            docActionRepository.updateCursor(data, username)
            _savedSelection.value = TextRange(positionToIndex(document.content, data.start), positionToIndex(document.content, data.end ?: data.start))
            _textFieldValue.value = textFieldValue.value.copy(
                selection = _savedSelection.value
            )
        }
    }

    private fun saveDocument() {
        viewModelScope.launch {
            docActionRepository.saveDocument()
            _uiEvent.send(UiEvent.ShowSnackBar("Document saved"))
        }
    }

    private fun closeDocument() {
        viewModelScope.launch {
            docActionRepository.closeDocument()
            _uiEvent.send(UiEvent.Navigate(to = Route.Home))
        }
    }
}