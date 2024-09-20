import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.dvir.docsync.auth.presentation.login.LoginEvent
import org.dvir.docsync.auth.presentation.login.LoginViewModel
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.UiEvent

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onAuthSuccess: () -> Unit,
    onNavigate: (Route) -> Unit
) {
    val state by loginViewModel.state
    val uiEventFlow = loginViewModel.uiEvent

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.to)
                is UiEvent.Success -> onAuthSuccess()
                is UiEvent.Error -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.background(
                    color = Color.Red
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.usernameTextField.text,
                onValueChange = {
                    loginViewModel.onEvent(LoginEvent.EnteredUsername(it))
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.passwordTextField.text,
                onValueChange = {
                    loginViewModel.onEvent(LoginEvent.EnteredPassword(it))
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    loginViewModel.onEvent(LoginEvent.OnLogin)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    loginViewModel.onEvent(LoginEvent.OnNavigateToSignup)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Don't have an account? Signup")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}
