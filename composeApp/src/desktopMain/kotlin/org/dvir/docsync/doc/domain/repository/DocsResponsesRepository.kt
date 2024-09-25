package org.dvir.docsync.doc.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import org.dvir.docsync.doc.data.responses.DocResponse

abstract class DocsResponsesRepository {
    abstract val responseFlow: SharedFlow<DocResponse>

    abstract fun connect()
}