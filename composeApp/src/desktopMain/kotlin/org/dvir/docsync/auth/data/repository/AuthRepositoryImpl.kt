package org.dvir.docsync.auth.data.repository

import org.dvir.docsync.auth.data.auth_type.AuthType
import org.dvir.docsync.auth.domain.data_source.AuthDataSource
import org.dvir.docsync.auth.domain.data_source.Token
import org.dvir.docsync.auth.domain.repository.AuthRepository
import org.dvir.docsync.auth.domain.service.TokenService
import org.dvir.docsync.core.result.Result

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val tokenService: TokenService
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<Token> {
        return authenticate(username, password, AuthType.Login)
    }

    override suspend fun signup(username: String, password: String): Result<Token> {
        return authenticate(username, password, AuthType.Signup)
    }

    private suspend fun authenticate(
        username: String,
        password: String,
        type: AuthType
    ): Result<Token> {
        return try {
            val result = if (type is AuthType.Login) authDataSource.login(username, password)
                else authDataSource.signup(username, password)

            if (result is Result.Error)
                return Result.Error(message = result.message ?: "Unknown error")

            tokenService.put(result.data!!)
            return Result.Success(result.data)
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Unknown error")
        }
    }
}