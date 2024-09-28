package org.dvir.docsync.doc.presentation.doc.viewmodel

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
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.domain.repository.DocActionRepository
import org.dvir.docsync.doc.domain.repository.DocsResponsesRepository
import org.dvir.docsync.doc.domain.model.Character

class DocViewModel(
    private val docActionRepository: DocActionRepository,
    private val docsResponsesRepository: DocsResponsesRepository,
    private val document: Document
) : ViewModel() {
    val cursorState = docActionRepository.getCursors()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            docsResponsesRepository.connect()
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

    fun onEvent(event: DocEvent) {
        when (event) {
            is DocEvent.AddCharacter -> handleAdd(DocConstants.OWN_USERNAME, event.char)
            is DocEvent.EditCharacter -> handleEdit(DocConstants.OWN_USERNAME, event.config)
            is DocEvent.RemoveCharacter -> handleRemove(DocConstants.OWN_USERNAME)
            is DocEvent.UpdateCursor -> handleCursorUpdate(DocConstants.OWN_USERNAME, event.cursorData)
            is DocEvent.SaveDocument -> saveDocument()
            DocEvent.CloseDocument -> closeDocument()
        }
    }
    fun getDocument(): Document = document

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
        }
    }

    private fun handleRemove(username: String) {
        viewModelScope.launch {
            docActionRepository.removeCharacter(username)
        }
    }

    private fun handleEdit(username: String, config: CharacterConfig) {
        viewModelScope.launch {
            docActionRepository.editCharacter(username, config)
        }
    }

    private fun handleCursorUpdate(username: String, data: CursorData) {
        viewModelScope.launch {
            docActionRepository.updateCursor(data, username)
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