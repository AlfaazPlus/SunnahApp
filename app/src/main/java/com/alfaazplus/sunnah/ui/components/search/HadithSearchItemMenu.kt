package com.alfaazplus.sunnah.ui.components.search

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenuItem
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.components.library.AddToCollectionSheet
import com.alfaazplus.sunnah.ui.components.reader.ACTION_ADD_TO_BOOKMARK
import com.alfaazplus.sunnah.ui.components.reader.ACTION_ADD_TO_COLLECTION
import com.alfaazplus.sunnah.ui.components.reader.ACTION_COPY_HADITH_NUMBER
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.models.userdata.AddToCollectionRequest
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
private fun Items(
    isBookmarked: Boolean,
    onItemClick: (String) -> Unit,
) {
    BottomSheetMenuItem(
        text = stringResource(R.string.copy_hadith_number),
        icon = R.drawable.ic_clipboard,
    ) {
        onItemClick(ACTION_COPY_HADITH_NUMBER)
    }
    BottomSheetMenuItem(
        text = if (isBookmarked) stringResource(R.string.added_to_bookmarks) else stringResource(R.string.add_to_bookmarks),
        icon = if (isBookmarked) R.drawable.ic_bookmark_check else R.drawable.ic_bookmark_plus,
    ) {
        onItemClick(ACTION_ADD_TO_BOOKMARK)
    }
    BottomSheetMenuItem(
        text = stringResource(R.string.add_to_collection),
        icon = R.drawable.ic_library,
    ) {
        onItemClick(ACTION_ADD_TO_COLLECTION)
    }
}

@Composable
fun HadithSearchItemMenu(
    item: HadithSearchResult,
    isOpen: Boolean,
    onClose: () -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val isBookmarked by viewModel
        .isBookmarked(
            item.hadith.collectionId,
            item.hadith.bookId,
            item.hadith.hadithNumber,
        )
        .collectAsState()

    val collectionModalController = rememberModalController<AddToCollectionRequest>()
    val bookmarksModalController = rememberModalController<AddToBookmarkRequest>()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val copiedMessage = stringResource(R.string.copied_to_clipboard)

    AddToCollectionSheet(collectionModalController)
    AddToBookmarksSheet(bookmarksModalController)

    BottomSheetMenu(
        title = "${item.collectionName}: ${item.hadith.hadithNumber}",
        isOpen = isOpen,
        onDismiss = onClose,
        headerArrangement = Arrangement.Start,
    ) {
        Items(
            isBookmarked,
        ) { actionType ->
            when (actionType) {
                ACTION_ADD_TO_COLLECTION -> {
                    collectionModalController.show(
                        AddToCollectionRequest(
                            hadithCollectionId = item.hadith.collectionId,
                            hadithBookId = item.hadith.bookId,
                            hadithNumber = item.hadith.hadithNumber,
                        )
                    )
                }

                ACTION_ADD_TO_BOOKMARK -> {
                    scope.launch {
                        if (!isBookmarked) {
                            viewModel.repo.addUserBookmark(
                                UserBookmark(
                                    hadithCollectionId = item.hadith.collectionId,
                                    hadithBookId = item.hadith.bookId,
                                    hadithNumber = item.hadith.hadithNumber,
                                    remark = "",
                                )
                            )
                        }

                        withContext(Dispatchers.Main) {
                            bookmarksModalController.show(
                                AddToBookmarkRequest(
                                    hadithCollectionId = item.hadith.collectionId,
                                    hadithBookId = item.hadith.bookId,
                                    hadithNumber = item.hadith.hadithNumber,
                                )
                            )
                        }
                    }
                }

                ACTION_COPY_HADITH_NUMBER -> {
                    clipboardManager.setClip(ClipEntry(ClipData.newPlainText("", "${item.collectionName}: ${item.hadith.hadithNumber}")))
                    MessageUtils.showClipboardMessage(
                        context,
                        text = copiedMessage,
                    )
                }
            }

            onClose()
        }
    }
}