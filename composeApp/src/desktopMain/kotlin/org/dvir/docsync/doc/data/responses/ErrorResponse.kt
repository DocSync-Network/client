package org.dvir.docsync.doc.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("error")
data class ErrorResponse(
    val message: String
)
