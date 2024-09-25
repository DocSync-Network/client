package org.dvir.docsync.doc.domain.repository

import org.dvir.docsync.doc.domain.model.ID

interface DocListRepository {
    suspend fun getAllDocs()
    suspend fun createDoc(docName: String)
    suspend fun removeDoc(docId: ID)
    suspend fun getDoc(docId: ID)
}
