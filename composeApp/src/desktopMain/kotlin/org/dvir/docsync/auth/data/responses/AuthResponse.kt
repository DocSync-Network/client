package org.dvir.docsync.auth.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthResponse {
    @Serializable
    @SerialName("auth")
    class AuthCompleted(val token: String): AuthResponse
    @Serializable
    @SerialName("error")
    class ErrorResponse(val error: String): AuthResponse
}