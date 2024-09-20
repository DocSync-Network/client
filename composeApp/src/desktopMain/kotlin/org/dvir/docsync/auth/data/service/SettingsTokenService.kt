package org.dvir.docsync.auth.data.service

import com.russhwolf.settings.Settings
import org.dvir.docsync.auth.domain.service.TokenService

class SettingsTokenService: TokenService {
    private val settings: Settings = Settings()

    override fun put(token: String) {
        settings.putString("token", token)
    }

    override fun get(): String? {
        return settings.getStringOrNull("token")
    }

    override fun clear() {
        settings.remove("token")
    }
}