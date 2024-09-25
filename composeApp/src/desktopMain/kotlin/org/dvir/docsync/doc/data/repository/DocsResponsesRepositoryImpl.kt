package org.dvir.docsync.doc.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.dvir.docsync.auth.domain.service.TokenService
import org.dvir.docsync.doc.data.data_source.DocsDataSource
import org.dvir.docsync.doc.data.responses.DocResponse
import org.dvir.docsync.doc.domain.repository.DocsResponsesRepository

class DocsResponsesRepositoryImpl(
    private val docsDataSource: DocsDataSource,
    private val tokenService: TokenService
): DocsResponsesRepository() {
    private val _responseFlow = MutableSharedFlow<DocResponse>()
    override val responseFlow: SharedFlow<DocResponse> = _responseFlow.asSharedFlow()

    override fun connect() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = tokenService.get()!!
            docsDataSource.connect(token).collect { response ->
                _responseFlow.emit(response)
            }
        }
    }
}