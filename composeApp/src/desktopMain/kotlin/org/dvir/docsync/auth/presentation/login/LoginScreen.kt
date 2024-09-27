package org.dvir.docsync.auth.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dvir.docsync.auth.presentation.login.viewmodel.LoginEvent
import org.dvir.docsync.auth.presentation.login.viewmodel.LoginViewModel
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.*

@Composable
fun LoginScreen(
    viewmodel: LoginViewModel,
    onAuthSuccess: () -> Unit,
    onNavigate: (Route) -> Unit
) {
    val uiEventFlow = viewmodel.uiEvent

    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.to)
                is UiEvent.Success -> onAuthSuccess()
                is UiEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { focusManager.clearFocus() }
            ),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        backgroundColor = ErrorColor,
                        actionColor = BackgroundColor
                    )
                }
            )
        },
        backgroundColor = Color(0xFFFFFFFF)
    ) { paddingValues ->
        if (viewmodel.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 168.dp)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome Back",
                    style = TextStyle(fontSize = 36.sp, color = Color.DarkGray),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Log in and continue collaborating on your docs",
                    style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                PrimaryTextField(
                    value = viewmodel.usernameTextField.value.text,
                    onValueChange = { value ->
                        viewmodel.onEvent(
                            LoginEvent.EnteredUsername(value)
                        )
                    },
                    hint = "Username",
                    isHintVisible = viewmodel.usernameTextField.value.isHintVisible,
                    onFocusChange = {
                        viewmodel.onEvent(
                            LoginEvent.ChangedUsernameFocus(it)
                        )
                    },
                    icon = Icons.Default.Person,
                )

                Spacer(Modifier.height(16.dp))

                PrimaryTextField(
                    value = viewmodel.passwordTextField.value.text,
                    onValueChange = { value ->
                        viewmodel.onEvent(
                            LoginEvent.EnteredPassword(value)
                        )
                    },
                    hint = "Password",
                    isHintVisible = viewmodel.passwordTextField.value.isHintVisible,
                    onFocusChange = {
                        viewmodel.onEvent(
                            LoginEvent.ChangedPasswordFocus(it)
                        )
                    },
                    icon = Icons.Default.Lock,
                    isPassword = true,
                )

                PrimaryButton(
                    text = "Continue",
                    onClick = {
                        viewmodel.onEvent(
                            LoginEvent.OnLogin
                        )
                    },
                    modifier = Modifier.width(240.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(text = "New to DocSync?")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign Up",
                        color = PrimaryColor,
                        modifier = Modifier.clickable {
                            viewmodel.onEvent(
                                LoginEvent.OnNavigateToSignup
                            )
                        }
                    )
                }
            }
        }
    }
}
