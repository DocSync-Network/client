package org.dvir.docsync

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import org.dvir.docsync.auth.data.data_source.AuthDataSourceImpl
import org.dvir.docsync.auth.data.repository.AuthRepositoryImpl
import org.dvir.docsync.auth.data.service.SettingsTokenService
import org.dvir.docsync.auth.domain.data_source.AuthDataSource
import org.dvir.docsync.auth.domain.repository.AuthRepository
import org.dvir.docsync.auth.domain.service.TokenService

object ServiceLocator {
    val services = mutableMapOf<Class<*>, Any>()

    private inline fun <reified T : Any> register(service: T) {
        services[T::class.java] = service
    }

    inline fun <reified T : Any> get(): T {
        return services[T::class.java] as? T
            ?: throw IllegalStateException("Service ${T::class.java.simpleName} not found: \n1) check the setup function\n2) check if the setup function called during setup")
    }

    fun setup() {
        val tokenService = SettingsTokenService()
        register(tokenService)

        val httpClient = HttpClient(CIO) {
            install(WebSockets)
        }
        val dataSource = AuthDataSourceImpl(httpClient)
        register<AuthDataSource>(dataSource)

        val authRepository = AuthRepositoryImpl(
            authDataSource = get<AuthDataSource>(),
            tokenService = get<TokenService>()
        )
        register<AuthRepository>(authRepository)
    }
}