package org.dvir.docsync.auth.domain.data_source

import org.dvir.docsync.core.result.Result

typealias Token = String

interface AuthDataSource {
    suspend fun login(username: String, password: String): Result<Token>
    suspend fun signup(username: String, password: String): Result<Token>
}