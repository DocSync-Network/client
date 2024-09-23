package org.dvir.docsync

import org.dvir.docsync.auth.presentation.login.LoginScreen
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.dvir.docsync.auth.presentation.login.viewmodel.LoginViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val loginViewModel: LoginViewModel = koinViewModel()
        LoginScreen(
            viewmodel = loginViewModel,
            onAuthSuccess = {
                println("Login success")
            },
            onNavigate = {
                println("Navigate to ${it.name}")
            }
        )
    }
}