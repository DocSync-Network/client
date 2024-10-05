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
import org.dvir.docsync.auth.presentation.splash.SplashViewModel
import org.dvir.docsync.doc.data.data_source.DocsDataSource
import org.dvir.docsync.doc.data.repository.DocsResponsesRepositoryImpl
import org.dvir.docsync.doc.data.repository.DocActionRepositoryImpl
import org.dvir.docsync.doc.data.repository.DocListRepositoryImpl
import org.dvir.docsync.doc.domain.cursor.CursorManager
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.domain.repository.DocActionRepository
import org.dvir.docsync.doc.domain.repository.DocListRepository
import org.dvir.docsync.doc.domain.repository.DocsResponsesRepository
import org.dvir.docsync.doc.presentation.doc.viewmodel.DocViewModel
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation)
        install(Logging)
        install(WebSockets)
    }

    // auth
    single<TokenService> { SettingsTokenService() }
    single<AuthDataSource> {
        AuthDataSourceImpl(httpClient)
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            authDataSource = get<AuthDataSource>(),
            tokenService = get<TokenService>(),
        )
    }
    viewModel<SplashViewModel> {
        SplashViewModel(authRepository = get<AuthRepository>())
    }
    viewModel<LoginViewModel> {
        LoginViewModel(authRepository = get<AuthRepository>())
    }
    viewModel<SignupViewModel> {
        SignupViewModel(authRepository = get<AuthRepository>())
    }

    // docs
    single<DocsDataSource> {
        DocsDataSource(httpClient)
    }
    single<DocListRepository> {
        DocListRepositoryImpl(dataSource = get<DocsDataSource>())
    }
    single<DocsResponsesRepository> {
        DocsResponsesRepositoryImpl(
            docsDataSource = get<DocsDataSource>(),
            tokenService = get<TokenService>(),
        )
    }
    factory<DocActionRepository> { (document: Document, cursorManager: CursorManager) ->
        DocActionRepositoryImpl(
            dataSource = get<DocsDataSource>(),
            cursorManager = cursorManager,
            document = document
        )
    }
    viewModel<HomeViewModel> {
        HomeViewModel(
            docListRepository = get<DocListRepository>(),
            docsResponsesRepository = get<DocsResponsesRepository>()
        )
    }
    viewModel { (document: Document, cursorManager: CursorManager) ->
        DocViewModel(
            docActionRepository = get { parametersOf(document, cursorManager) },
            docsResponsesRepository = get(),
            document = document
        )
    }
}