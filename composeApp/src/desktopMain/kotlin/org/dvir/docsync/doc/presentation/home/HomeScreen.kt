package org.dvir.docsync.doc.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dvir.docsync.core.navigation.Route
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
                DocumentCard(document, viewmodel)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewmodel.onEvent(
                    HomeEvent.OpenCreateDialog
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Document")
        }
    }

    if (viewmodel.isCreateDialogOpened.value) {
        CreateDocDialog(
            viewModel = viewmodel,
            onDismiss = { viewmodel.onEvent(HomeEvent.OpenCreateDialog) },
            onCreate = { viewmodel.onEvent(HomeEvent.CreateDoc) }
        )
    }
}

@Composable
fun DocumentCard(document: Document, viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp))
            .clickable {
                viewModel.onEvent(HomeEvent.GetDoc(document.id))
            }
            .padding(16.dp)
    ) {
        Text(text = document.name, style = MaterialTheme.typography.h6)

        Text(
            text = "Owner: ${document.owner}",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(top = 8.dp)
        )

        val creationDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(document.creationDate),
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

        Text(
            text = "Created on: $creationDate",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(top = 8.dp)
        )
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
