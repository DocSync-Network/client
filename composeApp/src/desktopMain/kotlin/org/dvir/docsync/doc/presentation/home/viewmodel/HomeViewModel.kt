package org.dvir.docsync.doc.presentation.home.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.UiEvent
import org.dvir.docsync.doc.data.responses.DocListResponse
import org.dvir.docsync.doc.data.responses.DocResponse
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.domain.repository.DocListRepository
import org.dvir.docsync.doc.domain.repository.DocsResponsesRepository

class HomeViewModel(
    private val docListRepository: DocListRepository,
    private val docsResponsesRepository: DocsResponsesRepository
): ViewModel() {

    var docs = mutableStateOf(emptyList<Document>())
        private set

    var isCreateDialogOpened = mutableStateOf(false)
        private set
    var createDialogDocName = mutableStateOf("")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            async { docsResponsesRepository.connect() }.await()
            docListRepository.getAllDocs()
            docsResponsesRepository.responseFlow.collect { response ->
                when (response) {
                    is DocResponse.ListResponse -> {
                        when (val listResponse = response.response) {
                            is DocListResponse.Doc -> {
                                _uiEvent.send(
                                    UiEvent.Navigate(
                                        to = Route.Doc(doc = listResponse.doc)
                                    )
                                )
                            }
                            is DocListResponse.Docs -> {
                                docs.value = listResponse.docs
                                println(docs.value)
                            }
                        }
                    }
                    is DocResponse.Error -> {
                        _uiEvent.send(
                            UiEvent.Error(
                                message = response.errorResponse.message
                            )
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                HomeEvent.CreateDoc -> {
                    docListRepository.createDoc(createDialogDocName.value)
                    isCreateDialogOpened.value = false
                }
                HomeEvent.OpenCreateDialog -> {
                    isCreateDialogOpened.value = true
                }
                is HomeEvent.DeleteDoc -> {
                    docListRepository.removeDoc(event.docId)
                }
                is HomeEvent.GetDoc -> {
                    docListRepository.getDoc(event.docId)
                }
            }
        }
    }
}