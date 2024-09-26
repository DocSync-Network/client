package org.dvir.docsync.doc.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.BackgroundColor
import org.dvir.docsync.core.ui.PrimaryColor
import org.dvir.docsync.core.ui.SecondaryColor
import org.dvir.docsync.core.ui.TextColor
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
        }
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
                viewModel = viewmodel,
                onDismiss = { viewmodel.onEvent(HomeEvent.OpenCreateDialog) },
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
            .background(SecondaryColor)
            .clickable(
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = document.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(40.dp))
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = TextColor,
                    modifier = Modifier.clickable {
                        onDelete()
                    }
                )
            }

            Text(
                text = "Owner: ${document.owner}",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            val creationDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(document.creationDate),
                ZoneId.systemDefault()
            ).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

            Text(
                text = creationDate,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun CreateDocDialog(viewModel: HomeViewModel, onDismiss: () -> Unit, onCreate: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Document") },
        text = {
            Column {
                OutlinedTextField(
                    value = viewModel.createDialogDocName.value,
                    onValueChange = { viewModel.createDialogDocName.value = it },
                    label = { Text("Document Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onCreate) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
