package org.dvir.docsync.auth.presentation.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.UiEvent

@Composable
fun SplashScreen(
    viewmodel: SplashViewModel,
    onAuthSuccess: () -> Unit,
    onNavigate: (Route) -> Unit
) {
    val uiEventFlow = viewmodel.uiEvent

    LaunchedEffect(Unit) {
        uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.to)
                is UiEvent.Success -> onAuthSuccess()
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "DocSync",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
