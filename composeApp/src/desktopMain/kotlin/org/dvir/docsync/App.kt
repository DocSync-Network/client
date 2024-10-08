package org.dvir.docsync

import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.dvir.docsync.auth.presentation.login.LoginScreen
import org.dvir.docsync.auth.presentation.signup.SignupScreen
import org.dvir.docsync.auth.presentation.splash.SplashScreen
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.doc.domain.cursor.CursorManager
import org.dvir.docsync.doc.presentation.doc.DocScreen
import org.dvir.docsync.doc.presentation.doc.viewmodel.DocViewModel
import org.dvir.docsync.doc.presentation.home.HomeScreen
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screen: Route by remember { mutableStateOf(Route.Splash) }

        Crossfade(targetState = screen) { currentScreen ->
            when (currentScreen) {
                Route.Login -> {
                    LoginScreen(
                        viewmodel = koinViewModel(),
                        onAuthSuccess = {
                            screen = Route.Home
                        },
                        onNavigate = { route ->
                            screen = route
                        }
                    )
                }
                Route.Signup -> {
                    SignupScreen(
                        viewmodel = koinViewModel(),
                        onAuthSuccess = {
                            screen = Route.Home
                        },
                        onNavigate = { route ->
                            screen = route
                        }
                    )
                }
                Route.Splash -> {
                    SplashScreen(
                        viewmodel = koinViewModel(),
                        onAuthSuccess = {
                            screen = Route.Home
                        },
                        onNavigate = { route ->
                            screen = route
                        }
                    )
                }
                Route.Home -> {
                    HomeScreen(
                        viewmodel = koinInject<HomeViewModel>(),
                        onNavigateToDoc = { route ->
                            screen = route
                        }
                    )
                }
                is Route.Doc -> {
                    DocScreen(
                        viewModel = koinInject<DocViewModel>(
                            parameters = {
                                parametersOf(
                                    currentScreen.doc,
                                    CursorManager()
                                )
                            }
                        ),
                        document = currentScreen.doc,
                        onNavigateBack = {
                            screen = Route.Home
                        }
                    )
                }
            }
        }
    }
}