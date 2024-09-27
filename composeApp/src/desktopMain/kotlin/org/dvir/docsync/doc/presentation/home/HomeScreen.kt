package org.dvir.docsync.doc.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.*
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeEvent
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewmodel: HomeViewModel,
    onNavigateToDoc: (Route.Doc) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewmodel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Error -> {
                    snackbarHostState.showSnackbar(
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
            CreateDocDialog(
                nameValue = viewmodel.createDialogDocName.value,
                onNameValueChange = {
                    viewmodel.createDialogDocName.value = it
                },
                onDismiss = { viewmodel.onEvent(HomeEvent.CloseCreateDialog) },
                onCreate = { viewmodel.onEvent(HomeEvent.CreateDoc) }
            )
        }
    }
}

@Composable
fun DocumentCard(
    document: Document,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SecondaryColor)
            .clickable(
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = document.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextColor,
                    modifier = Modifier
                        .clickable { onDelete() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Owner: ${document.owner}",
                fontSize = 16.sp,
                color = TextColor,
                modifier = Modifier.padding(top = 4.dp)
            )

            val creationDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(document.creationDate),
                ZoneId.systemDefault()
            ).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))

            Text(
                text = creationDate,
                fontSize = 16.sp,
                color = TextColor
            )
        }
    }
}

@Composable
fun CreateDocDialog(
    nameValue: String,
    onNameValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text(
                    text = "Create New Document",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nameValue,
                    onValueChange = onNameValueChange,
                    label = { Text("Document Name") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PrimaryColor
                    ),
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PrimaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(25.dp),
                onClick = onCreate
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PrimaryColor)
            }
        }
    )
}
