package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.Section
import com.alfaazplus.sunnah.ui.components.common.SectionEmptyMessage
import com.alfaazplus.sunnah.ui.components.common.SectionHeaderActionButton
import com.alfaazplus.sunnah.ui.components.common.SectionHeaderViewAll
import com.alfaazplus.sunnah.ui.components.library.CreateCollectionSheet
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel

@Composable
private fun SectionCollections(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var showCreateCollectionSheet by remember { mutableStateOf(false) }
    val userCollections by viewModel.userCollections.collectAsState()

    Section(
        icon = R.drawable.ic_library,
        title = "Collections",
        headerRightContent = {
            if (userCollections.isNotEmpty()) {
                SectionHeaderActionButton(
                    icon = R.drawable.ic_add,
                    text = "New",
                ) {
                    showCreateCollectionSheet = true
                }
                SectionHeaderViewAll {
                    // TODO: Implement view all action
                }
            }
        },
    ) {
        if (userCollections.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
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
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(userCollections.size) {
                    val collection = userCollections[it]
                    Card(
                        onClick = {},
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier
                            .heightIn(max = 92.dp)
                            .widthIn(min = 160.dp, max = 240.dp)
                            .fillMaxHeight(),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = collection.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )

                            if (collection.description != null) {
                                Text(
                                    text = collection.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    CreateCollectionSheet(
        showCreateCollectionSheet,
        onCancel = {
            showCreateCollectionSheet = false
        },
        onCreate = {
            showCreateCollectionSheet = false
        },
    )
}

@Composable
fun LibraryScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
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