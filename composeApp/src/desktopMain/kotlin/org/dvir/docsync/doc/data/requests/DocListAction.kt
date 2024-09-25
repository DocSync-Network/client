package org.dvir.docsync.doc.data.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.dvir.docsync.doc.domain.model.ID

@Serializable
sealed class DocListAction {
    @Serializable
    @SerialName("getDoc")
    data class GetDoc(val docId: ID) : DocListAction()
    @Serializable
    @SerialName("createDoc")
    data class CreateDoc(val docName: String) : DocListAction()
    @Serializable
    @SerialName("removeDoc")
    data class RemoveDoc(val docId: ID) : DocListAction()
    @Serializable
    @SerialName("getAllDocs")
    data object GetAllDocs : DocListAction()
}