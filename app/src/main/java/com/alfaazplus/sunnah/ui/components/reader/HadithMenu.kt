package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.components.library.AddToCollectionSheet
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.models.userdata.AddToCollectionRequest
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ACTION_ADD_TO_BOOKMARK = "add_to_bookmark"
const val ACTION_ADD_TO_COLLECTION = "add_to_collection"
const val ACTION_SHARE_HADITH = "share_hadith"
const val ACTION_COPY_HADITH_TEXT = "copy_hadith_text"
const val ACTION_REPORT_ISSUE = "report_issue"
const val ACTION_COPY_HADITH_NUMBER = "copy_hadith_number"

@Composable
private fun Item(
    text: String,
    textColor: Color? = null,
    icon: Int,
    iconTint: Color? = null,
    onClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.background,
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(id = icon),
            tint = iconTint ?: LocalContentColor.current,
            contentDescription = text,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor ?: MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun Items(
    isBookmarked: Boolean,
    onItemClick: (String) -> Unit,
) { // TODO: Add actions
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Item(
                text = if (isBookmarked) stringResource(R.string.added_to_bookmarks) else stringResource(R.string.add_to_bookmarks),
                icon = if (isBookmarked) R.drawable.ic_bookmark_check else R.drawable.ic_bookmark_plus,
                textColor = if (isBookmarked) MaterialTheme.colorScheme.primary else null,
                iconTint = if (isBookmarked) MaterialTheme.colorScheme.primary else null,
                onClick = {
                    onItemClick(ACTION_ADD_TO_BOOKMARK)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.add_to_collection),
                icon = R.drawable.ic_library,
                onClick = {
                    onItemClick(ACTION_ADD_TO_COLLECTION)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.share_this_hadith),
                icon = R.drawable.ic_share,
                onClick = {
                    onItemClick(ACTION_SHARE_HADITH)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.copy_hadith_text),
                icon = R.drawable.ic_clipboard,
                onClick = {
                    onItemClick(ACTION_COPY_HADITH_TEXT)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.report_issue),
                icon = R.drawable.ic_flag,
                onClick = {
                    onItemClick(ACTION_REPORT_ISSUE)
                },
            )
        }
    }
}

@Composable
fun HadithMenu(
    collectionId: Int,
    bookId: Int,
    hadithNumber: String,
    isOpen: Boolean,
    onClose: () -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val isBookmarked by viewModel
        .isBookmarked(collectionId, bookId, hadithNumber)
        .collectAsState()

    val collectionModalController = rememberModalController<AddToCollectionRequest>()
    val bookmarksModalController = rememberModalController<AddToBookmarkRequest>()
    val scope = rememberCoroutineScope()

    AddToCollectionSheet(collectionModalController)
    AddToBookmarksSheet(bookmarksModalController)

    BottomSheetMenu(
        title = stringResource(R.string.hadith_options),
        isOpen = isOpen,
        onDismiss = onClose,
    ) {
        Items(
            isBookmarked = isBookmarked
        ) { actionType ->
            when (actionType) {
                ACTION_ADD_TO_COLLECTION -> {
                    collectionModalController.show(
                        AddToCollectionRequest(
                            hadithCollectionId = collectionId,
                            hadithBookId = bookId,
                            hadithNumber = hadithNumber,
                        )
                    )
                }

                ACTION_ADD_TO_BOOKMARK -> {
                    scope.launch {
                        if (!isBookmarked) {
                            viewModel.repo.addUserBookmark(
                                UserBookmark(
                                    hadithCollectionId = collectionId,
                                    hadithBookId = bookId,
                                    hadithNumber = hadithNumber,
                                    remark = "",
                                )
                            )
                        }

                        withContext(Dispatchers.Main) {
                            bookmarksModalController.show(
                                AddToBookmarkRequest(
                                    hadithCollectionId = collectionId,
                                    hadithBookId = bookId,
                                    hadithNumber = hadithNumber,
                                )
                            )
                        }
                    }

                }

                else -> { // Handle other actions if needed
                }
            }

            onClose()
        }
    }
}