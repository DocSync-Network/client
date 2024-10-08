package org.dvir.docsync.doc.data.repository

import org.dvir.docsync.doc.data.data_source.DocsDataSource
import org.dvir.docsync.doc.data.requests.DocListAction
import org.dvir.docsync.doc.domain.model.ID
import org.dvir.docsync.doc.domain.repository.DocListRepository

class DocListRepositoryImpl(
    private val dataSource: DocsDataSource
) : DocListRepository {
    override suspend fun getAllDocs() {
        dataSource.sendDocListAction(DocListAction.GetAllDocs)
    }

    override suspend fun createDoc(docName: String) {
        println("creating doc")
        dataSource.sendDocListAction(DocListAction.CreateDoc(docName))
    }

    override suspend fun removeDoc(docId: ID) {
        dataSource.sendDocListAction(DocListAction.RemoveDoc(docId))
    }

    override suspend fun getDoc(docId: ID) {
        dataSource.sendDocListAction(DocListAction.GetDoc(docId))
    }
}
