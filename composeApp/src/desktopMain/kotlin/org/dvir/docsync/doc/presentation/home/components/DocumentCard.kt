package org.dvir.docsync.doc.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dvir.docsync.core.ui.SecondaryColor
import org.dvir.docsync.core.ui.TextColor
import org.dvir.docsync.doc.domain.model.Document
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Delete,
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
