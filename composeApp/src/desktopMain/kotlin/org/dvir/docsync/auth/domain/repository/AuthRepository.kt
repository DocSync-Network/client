package org.dvir.docsync.auth.domain.repository

import org.dvir.docsync.auth.domain.data_source.Token
import org.dvir.docsync.core.result.Result

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Token>
    suspend fun signup(username: String, password: String): Result<Token>
    suspend fun validateToken(): Result<Boolean>
}
