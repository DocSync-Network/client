package org.dvir.docsync.doc.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.dvir.docsync.doc.domain.model.Document

@Serializable
sealed class DocListResponse {
    @Serializable
    @SerialName("doc")
    data class Doc(val doc: Document) : DocListResponse()
    @Serializable
    @SerialName("docs")
    data class Docs(val docs: List<Document>) : DocListResponse()
}