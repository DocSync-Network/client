package org.dvir.docsync

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.dvir.docsync.auth.presentation.signup.SignupScreen
import org.dvir.docsync.auth.presentation.signup.viewmodel.SignupViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: SignupViewModel = koinViewModel()
        SignupScreen(
            viewmodel = viewModel,
            onAuthSuccess = {
                println("Login success")
            },
            onNavigate = {
                println("Navigate to ${it.name}")
            }
        )
    }
}