package org.dvir.docsync.auth.domain.service

interface TokenService {
    fun put(token: String)
    fun get(): String?
    fun clear()
}
