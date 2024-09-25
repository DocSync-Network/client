package org.dvir.docsync.doc.data.responses

sealed class DocResponse {
    data class ListResponse(val response: DocListResponse) : DocResponse()
    data class ActionResponse(val response: DocActionResponse) : DocResponse()
    data class Error(val errorResponse: ErrorResponse) : DocResponse()

}
