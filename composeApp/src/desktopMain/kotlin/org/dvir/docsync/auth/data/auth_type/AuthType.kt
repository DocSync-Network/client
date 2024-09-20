package org.dvir.docsync.auth.data.auth_type

sealed interface AuthType {
    data object Login: AuthType
    data object Signup: AuthType
}