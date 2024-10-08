package org.dvir.docsync.doc.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.BackgroundColor
import org.dvir.docsync.core.ui.ErrorColor
import org.dvir.docsync.core.ui.PrimaryColor
import org.dvir.docsync.core.ui.UiEvent
import org.dvir.docsync.doc.presentation.components.DefaultDialog
import org.dvir.docsync.doc.presentation.home.components.DocumentCard
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeEvent
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewmodel: HomeViewModel,
    onNavigateToDoc: (Route.Doc) -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewmodel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Error -> {
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEvent.Navigate -> {
                    if (event.to is Route.Doc) {
                        onNavigateToDoc(event.to)
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                backgroundColor = PrimaryColor,
                onClick = {
                    viewmodel.onEvent(
                        HomeEvent.OpenCreateDialog
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add doc",
                    tint = BackgroundColor
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        backgroundColor = ErrorColor,
                        actionColor = BackgroundColor
                    )
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Recent documents",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewmodel.docs.value) { document ->
                    DocumentCard(
                        document,
                        onDelete = {
                            viewmodel.onEvent(
                                HomeEvent.DeleteDoc(document.id)
                            )
                        },
                        onClick = {
                            viewmodel.onEvent(
                                HomeEvent.GetDoc(document.id)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (viewmodel.isCreateDialogOpened.value) {
            DefaultDialog(
                header = "Create New Document",
                label = "Document Name",
                primaryButtonText = "Create",
                textFieldValue = viewmodel.createDialogDocName.value,
                onTextFieldValueChange = { value ->
                    viewmodel.createDialogDocName.value = value
                },
                onDismiss = { viewmodel.onEvent(HomeEvent.CloseCreateDialog) },
                onAccept = { viewmodel.onEvent(HomeEvent.CreateDoc) }
            )
        }
    }
}
