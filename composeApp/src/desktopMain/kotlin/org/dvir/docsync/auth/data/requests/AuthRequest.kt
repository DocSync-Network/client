package org.dvir.docsync.auth.data.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthRequest {
    @Serializable
    @SerialName("auth")
    data class Auth(
        val username: String,
        val password: String
    ): AuthRequest
}