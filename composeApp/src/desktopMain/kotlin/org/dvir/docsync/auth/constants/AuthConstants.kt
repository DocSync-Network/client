package org.dvir.docsync.auth.constants

import org.dvir.docsync.core.constants.Constants

object AuthConstants {
    const val AUTH_URL = "http://${Constants.API_URL}:${Constants.PORT}/auth/"
    const val LOGIN_ENDPOINT = "login"
    const val SIGNUP_ENDPOINT = "signup"
    const val VALIDATE_ENDPOINT = "authenticate"
}