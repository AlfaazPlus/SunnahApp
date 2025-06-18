package com.alfaazplus.sunnah.ui.components.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.launch
import java.util.Date

val COLOR_PRESETS = listOf(
    "#000000",
    "#2D2F3A",
    "#2F3B3B",
    "#003F2F",
    "#3B1C32",
    "#3D1E6D",
    "#0f4d8f",
    "#7a422c",
    "#5e2e5e",
    "#75692e",
    "#6e2b2b",
)

@Composable
private fun ColorSelector(
    selectedColor: String?,
    onColorSelected: (String) -> Unit,
    onReset: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.select_color),
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.primary,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .border(1.dp, if (selectedColor == null) colorScheme.primary else Color.Transparent, MaterialTheme.shapes.medium)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            onReset()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.circle_minus),
                        contentDescription = null,
                    )
                }
            }

            items(COLOR_PRESETS.size) { index ->
                val colorHex = COLOR_PRESETS[index]
                val color = Color(colorHex.toColorInt())

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            onColorSelected(colorHex)
                        }
                        .background(color),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedColor == colorHex) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    collectionToUpdate: UserCollection?,
    onClose: () -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var value by remember(collectionToUpdate) { mutableStateOf(collectionToUpdate?.name ?: "") }
    var description by remember(collectionToUpdate) { mutableStateOf(collectionToUpdate?.description ?: "") }
    var color by remember { mutableStateOf(collectionToUpdate?.color) }

    val bgColor = colorScheme.background
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
                  value = value,
                  onValueChange = {
                      value = it
                  },
                  singleLine = true,
                  label = { Text(stringResource(R.string.collection_name)) },
                  placeholder = { Text(stringResource(R.string.collection_name_hint)) },
                  shape = MaterialTheme.shapes.medium,
                  colors = TextFieldDefaults.colors(
                      focusedIndicatorColor = Color.Transparent,
                      unfocusedIndicatorColor = Color.Transparent,
                      unfocusedContainerColor = bgColor,
                      focusedContainerColor = bgColor,
                  )
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
                  value = description,
                  onValueChange = {
                      description = it
                  },
                  maxLines = 5,
                  minLines = 5,
                  label = { Text(stringResource(R.string.description)) },
                  placeholder = { Text(stringResource(R.string.collection_description_hint)) },
                  shape = MaterialTheme.shapes.medium,
                  colors = TextFieldDefaults.colors(
                      focusedIndicatorColor = Color.Transparent,
                      unfocusedIndicatorColor = Color.Transparent,
                      unfocusedContainerColor = bgColor,
                      focusedContainerColor = bgColor,
                  )
        )

        ColorSelector(
            selectedColor = color,
            onColorSelected = { selectedColor ->
                color = selectedColor
            },
            onReset = {
                color = null
            },
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surfaceVariant,
                contentColor = colorScheme.onSurfaceVariant,
            ),
        ) {
            Text(stringResource(R.string.cancel))
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                scope.launch {
                    if (collectionToUpdate != null) {
                        viewModel.repo.updateUserCollection(
                            collectionToUpdate.copy(
                                name = value,
                                description = if (description.isBlank()) null else description.trim(),
                                color = color,
                                updatedAt = Date(),
                            )
                        )
                    } else {
                        viewModel.repo.addUserCollection(
                            UserCollection(
                                name = value,
                                description = if (description.isBlank()) null else description.trim(),
                                color = color,
                            )
                        )
                    }

                    onClose()
                }
            },
            enabled = value.isNotBlank(),
        ) {
            Text(stringResource(if (collectionToUpdate != null) R.string.update else R.string.create))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateCollectionSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    collectionToUpdate: UserCollection? = null,
) {
    val sheetState = rememberModalBottomSheetState(true)

    if (!isOpen) return

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        contentColor = colorScheme.onSurface,
        dragHandle = {},
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    if (collectionToUpdate != null) R.string.update_collection else R.string.create_collection,
                ),
                style = MaterialTheme.typography.titleMedium,
            )

        }

        Content(
            collectionToUpdate = collectionToUpdate,
            onClose = onClose,
        )
    }
}