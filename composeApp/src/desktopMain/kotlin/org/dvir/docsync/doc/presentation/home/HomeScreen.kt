package org.dvir.docsync.doc.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeEvent
import org.dvir.docsync.doc.presentation.home.viewmodel.HomeViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewmodel: HomeViewModel
) {
    Column {
        Text("Docs")

        Button(
            onClick = {
                println("")
                viewmodel.onEvent(
                    HomeEvent.GetDoc("")
                )
            }
        ) {
            Text("Refresh")
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(250.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(viewmodel.docs.value) {
                Column {
                    Text(text = it.name)
                    Text(text = it.owner)
                    val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.creationDate), ZoneId.systemDefault())
                    Text(text = time.format(DateTimeFormatter.BASIC_ISO_DATE))
                }
            }
        }
    }
}