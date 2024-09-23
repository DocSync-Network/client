package org.dvir.docsync.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import org.dvir.docsync.auth.data.data_source.AuthDataSourceImpl
import org.dvir.docsync.auth.data.repository.AuthRepositoryImpl
import org.dvir.docsync.auth.data.service.SettingsTokenService
import org.dvir.docsync.auth.domain.data_source.AuthDataSource
import org.dvir.docsync.auth.domain.repository.AuthRepository
import org.dvir.docsync.auth.domain.service.TokenService
import org.dvir.docsync.auth.presentation.login.viewmodel.LoginViewModel
import org.dvir.docsync.auth.presentation.signup.viewmodel.SignupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // auth
    single<TokenService> { SettingsTokenService() }
    single<AuthDataSource> {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation)
            install(Logging)
            install(WebSockets)
        }
        AuthDataSourceImpl(httpClient)
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            authDataSource = get<AuthDataSource>(),
            tokenService = get<TokenService>(),
        )
    }
    viewModel<LoginViewModel> {
        LoginViewModel(authRepository = get<AuthRepository>())
    }
    viewModel<SignupViewModel> {
        SignupViewModel(authRepository = get<AuthRepository>())
    }
}