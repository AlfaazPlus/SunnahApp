package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.Section
import com.alfaazplus.sunnah.ui.components.common.SectionEmptyMessage
import com.alfaazplus.sunnah.ui.components.library.CreateCollectionSheet
import com.alfaazplus.sunnah.ui.theme.alpha

@Composable
private fun SectionCollections() {
    var showCreateCollectionSheet by remember { mutableStateOf(false) }

    Section(
        icon = R.drawable.ic_library,
        title = "Collections",
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.alpha(0.6f), MaterialTheme.shapes.medium)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "No collections created yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            OutlinedButton(
                onClick = {
                    showCreateCollectionSheet = true
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text = "Create Collection",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }

    CreateCollectionSheet(
        showCreateCollectionSheet,
        onCancel = {
            showCreateCollectionSheet = false
        },
        onCreate = { id -> // TODO: Handle collection creation logic here
            showCreateCollectionSheet = false
        },
    )
}

@Composable
fun LibraryScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Section(
            icon = R.drawable.ic_history,
            title = "Reading History",
        ) {
            SectionEmptyMessage(
                "Your reading history appears here."
            )
        }
        Section(
            icon = R.drawable.ic_bookmark,
            title = "Bookmarks",
        ) {
            SectionEmptyMessage(
                "No bookmarks yet."
            )
        }
        SectionCollections()
    }
}