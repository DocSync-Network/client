package org.dvir.docsync.auth.data.data_source

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.dvir.docsync.auth.data.auth_type.AuthType
import org.dvir.docsync.auth.data.requests.AuthRequest
import org.dvir.docsync.auth.data.responses.AuthResponse
import org.dvir.docsync.auth.domain.data_source.AuthDataSource
import org.dvir.docsync.auth.domain.data_source.Token
import org.dvir.docsync.auth.constants.AuthConstants
import org.dvir.docsync.core.result.Result

class AuthDataSourceImpl(
    private val client: HttpClient
) : AuthDataSource {
    override suspend fun login(username: String, password: String): Result<Token> {
        return authenticate(username, password, AuthType.Login)
    }

    override suspend fun signup(username: String, password: String): Result<Token> {
        return authenticate(username, password, AuthType.Signup)
    }

    override suspend fun validateToken(token: Token): Result<Boolean> {
        val response = client.get("${AuthConstants.AUTH_URL}${AuthConstants.VALIDATE_ENDPOINT}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        return if (response.status == HttpStatusCode.OK) {
            Result.Success(true)
        } else {
            Result.Success(false)
        }
    }

    private suspend fun authenticate(
        username: String,
        password: String,
        type: AuthType
    ): Result<Token> {
        val response: HttpResponse = client.post(
            "${AuthConstants.AUTH_URL}${if (type is AuthType.Signup) AuthConstants.SIGNUP_ENDPOINT else AuthConstants.LOGIN_ENDPOINT}"
        ){
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(AuthRequest.Auth(username = username, password = password)))
        }
        val authResponse = Json.decodeFromString<AuthResponse>(response.body())

        return if (authResponse is AuthResponse.ErrorResponse) {
            Result.Error(message = authResponse.error)
        } else {
            authResponse as AuthResponse.AuthCompleted
            return Result.Success(authResponse.token)
        }
    }
}
