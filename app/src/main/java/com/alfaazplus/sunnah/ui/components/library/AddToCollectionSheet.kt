package com.alfaazplus.sunnah.ui.components.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import com.alfaazplus.sunnah.ui.components.common.TextInput
import com.alfaazplus.sunnah.ui.controllers.ModalController
import com.alfaazplus.sunnah.ui.models.userdata.AddToCollectionRequest
import com.alfaazplus.sunnah.ui.screens.main.UserCollectionCard
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.launch

@Composable
private fun Content(
    request: AddToCollectionRequest,
    onCancel: () -> Unit,
    onCreate: () -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val userCollections by viewModel.userCollections.collectAsState()

    var remark by remember { mutableStateOf("") }

    var initialCollectionIds by remember { mutableStateOf(setOf<Long>()) }
    var selectedCollectionIds by remember { mutableStateOf(setOf<Long>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userCollections) {
        /**
         * Load current selected collections for the hadith.
         */
        viewModel.repo
            .loadCollectionsForHadith(
                hadithCollectionId = request.hadithCollectionId,
                hadithBookId = request.hadithBookId,
                hadithNumber = request.hadithNumber,
            )
            .collect { items ->
                val currentSelectionIds = items
                    .map { it.id }
                    .toSet()

                initialCollectionIds = currentSelectionIds
                selectedCollectionIds = currentSelectionIds
            }
    }

    val isDeleting = initialCollectionIds.isNotEmpty() && selectedCollectionIds.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextInput(
            value = remark,
            onValueChange = {
                remark = it
            },
            maxLines = 4,
            minLines = 2,
            label = stringResource(R.string.note),
            placeholder = stringResource(R.string.optional_note),
            bgColor = MaterialTheme.colorScheme.background,
        )

        Text(
            stringResource(R.string.select_collections),
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        LazyVerticalGrid(
            userScrollEnabled = false,
            columns = GridCells.Fixed(integerResource(R.integer.collection_grid_columns_2)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max = 1000.dp)
        ) {
            items(userCollections.size) {
                Box {
                    UserCollectionCard(userCollections[it]) { collection ->
                        val collectionId = collection.id

                        selectedCollectionIds = if (selectedCollectionIds.contains(collectionId)) {
                            selectedCollectionIds - collectionId
                        } else {
                            selectedCollectionIds + collectionId
                        }
                    }

                    if (selectedCollectionIds.contains(userCollections[it].id)) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.alpha(0.5f),
                                    shape = RoundedCornerShape(100),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                modifier = Modifier.size(15.dp),
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
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
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Text(stringResource(R.string.cancel))
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                scope.launch {
                    val removedCollectionIds = initialCollectionIds - selectedCollectionIds
                    val addedOrUpdatedCollectionIds = selectedCollectionIds

                    removedCollectionIds.forEach {
                        viewModel.repo.removeItemFromUserCollection(
                            hadithCollectionId = request.hadithCollectionId,
                            hadithBookId = request.hadithBookId,
                            hadithNumber = request.hadithNumber,
                            userCollectionId = it,
                        )
                    }

                    addedOrUpdatedCollectionIds.map {
                        viewModel.repo.addUserCollectionItem(
                            UserCollectionItem(
                                userCollectionId = it,
                                hadithCollectionId = request.hadithCollectionId,
                                hadithBookId = request.hadithBookId,
                                hadithNumber = request.hadithNumber,
                                remark = remark,
                            )
                        )
                    }

                    onCreate()
                }
            },
            enabled = isDeleting || selectedCollectionIds.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDeleting) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },

                contentColor = if (isDeleting) {
                    MaterialTheme.colorScheme.onError
                } else {
                    MaterialTheme.colorScheme.onPrimary
                },
            ),
        ) {
            Text(
                if (isDeleting) stringResource(R.string.remove)
                else if (initialCollectionIds.isEmpty()) stringResource(R.string.add_to_collection)
                else stringResource(R.string.update)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCollectionSheet(
    controller: ModalController<AddToCollectionRequest>,
) {
    val request = controller.data
    val sheetState = rememberModalBottomSheetState(true) { sheetValue ->
        when (sheetValue) {
            SheetValue.Hidden -> false
            SheetValue.Expanded,
            SheetValue.PartiallyExpanded,
                -> true
        }
    }

    if (!controller.isVisible) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = {
            controller.hide()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
        ),
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
                text = stringResource(R.string.add_to_collection),
                style = MaterialTheme.typography.titleMedium,
            )

        }

        if (request != null) {
            Content(
                request = request,
                onCancel = {
                    controller.hide()
                },
                onCreate = {
                    controller.hide()
                },
            )
        }
    }
}