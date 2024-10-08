package org.dvir.docsync.core.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.util.Base64

object UserData {
    lateinit var username: String

    fun getUsernameFromJWT(token: String): String? {
        try {
            val parts = token.split(".")
            if (parts.size != 3) {
                return null
            }

            val payload = String(Base64.getDecoder().decode(parts[1]))
            val jsonPayload = Json.parseToJsonElement(payload) as JsonObject

            return jsonPayload["username"]?.toString()?.replace("\"", "")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}