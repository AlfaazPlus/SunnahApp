package com.alfaazplus.sunnah.ui.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.IconButton
import com.alfaazplus.sunnah.ui.components.common.TextButton
import com.alfaazplus.sunnah.ui.components.common.TextIconButton
import com.alfaazplus.sunnah.ui.components.common.TextInput
import com.alfaazplus.sunnah.ui.controllers.ModalController
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.HadithRepoViewModel
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@Composable
private fun Content(
    request: AddToBookmarkRequest,
    closeSheet: () -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
    hadithViewModel: HadithRepoViewModel = hiltViewModel(),
) {
    val hadithCollection = produceState<CollectionWithInfo?>(null) {
        value = hadithViewModel.repo.getCollection(request.hadithCollectionId)
    }.value

    val hadithBook = produceState<BookWithInfo?>(null) {
        value = hadithViewModel.repo.getBookById(request.hadithCollectionId, request.hadithBookId)
    }.value

    val oBookmark by viewModel.repo
        .observeUserBookmark(
            request.hadithCollectionId,
            request.hadithBookId,
            request.hadithNumber,
        )
        .collectAsState(initial = null)

    var isEditing by remember { mutableStateOf(request.editMode) }
    var remark by remember { mutableStateOf(oBookmark?.remark ?: "") }

    val scope = rememberCoroutineScope()
    val navController = LocalNavHostController.current

    LaunchedEffect(oBookmark) {
        if (remark.isNotEmpty()) {
            return@LaunchedEffect
        }

        remark = oBookmark?.remark ?: ""
    }

    val bookmark = oBookmark ?: return

    Column(
        modifier = Modifier.fillMaxHeight(0.9f),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.bookmarks),
                style = MaterialTheme.typography.titleMedium,
            )

            if (!isEditing) {
                TextIconButton(
                    painter = painterResource(R.drawable.pencil_line),
                    text = stringResource(R.string.edit),
                    small = true,
                ) {
                    isEditing = true
                }

                IconButton(
                    painter = painterResource(R.drawable.ic_delete),
                    small = true,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError
                    ),
                ) {
                    scope.launch {
                        viewModel.repo.deleteUserBookmark(bookmark.id)

                        withContext(Dispatchers.Main) {
                            closeSheet()
                        }
                    }
                }
            } else {
                TextButton(
                    text = stringResource(R.string.done),
                    small = true,
                    colors = ButtonDefaults.buttonColors(),
                ) {
                    isEditing = false
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {

                    Text(
                        text = "${hadithCollection?.info?.name} : ${request.hadithNumber}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Book: ${hadithBook?.info?.title}",
                    )
                }
            }

            Text(
                stringResource(R.string.note),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            if (isEditing) {
                TextInput(
                    value = remark,
                    onValueChange = {
                        remark = it

                        scope.launch {
                            viewModel.repo.updateUserBookmark(
                                bookmark.copy(
                                    remark = it,
                                    updatedAt = Date(),
                                ),
                            )
                        }
                    },
                    minLines = 4,
                    placeholder = stringResource(R.string.optional_note),
                    bgColor = MaterialTheme.colorScheme.background,
                )
            } else if (bookmark.remark.isNotBlank()) {
                Text(
                    text = bookmark.remark,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = stringResource(R.string.no_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (!isEditing && request.openInReader) {
            TextButton(
                text = stringResource(R.string.open_in_reader),
                fullWidth = true,
                modifier = Modifier.padding(16.dp),
            ) {
                navController.navigate(
                    Routes.READER.args(
                        request.hadithCollectionId,
                        request.hadithBookId,
                        request.hadithNumber,
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBookmarksSheet(
    controller: ModalController<AddToBookmarkRequest>,
) {
    val request = controller.data
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

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
        dragHandle = {},
    ) {
        if (request != null) {
            Content(request = request, closeSheet = {
                controller.hide()
            })
        }
    }
}