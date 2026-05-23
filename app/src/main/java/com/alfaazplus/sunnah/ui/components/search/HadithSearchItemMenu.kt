package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenuItem
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.components.library.AddToCollectionSheet
import com.alfaazplus.sunnah.ui.components.reader.HadithMenuAction
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.models.userdata.AddToCollectionRequest
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
private fun Items(
    isBookmarked: Boolean,
    onItemClick: (HadithMenuAction) -> Unit,
) {
    BottomSheetMenuItem(
        text = stringResource(R.string.copy_hadith_number),
        icon = R.drawable.ic_clipboard,
    ) {
        onItemClick(HadithMenuAction.COPY_HADITH_NUMBER)
    }
    BottomSheetMenuItem(
        text = if (isBookmarked) stringResource(R.string.added_to_bookmarks) else stringResource(R.string.add_to_bookmarks),
        icon = if (isBookmarked) R.drawable.ic_bookmark_check else R.drawable.ic_bookmark_plus,
    ) {
        onItemClick(HadithMenuAction.ADD_TO_BOOKMARK)
    }
    BottomSheetMenuItem(
        text = stringResource(R.string.add_to_collection),
        icon = R.drawable.ic_library,
    ) {
        onItemClick(HadithMenuAction.ADD_TO_COLLECTION)
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
        .isBookmarked(item.hadithId)
        .collectAsState()

    val collectionModalController = rememberModalController<AddToCollectionRequest>()
    val bookmarksModalController = rememberModalController<AddToBookmarkRequest>()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.copied_to_clipboard)

    AddToCollectionSheet(collectionModalController)
    AddToBookmarksSheet(bookmarksModalController)

    BottomSheetMenu(
        title = "${item.collectionName}: ${item.hadithNumber}",
        isOpen = isOpen,
        onDismiss = onClose,
        headerArrangement = Arrangement.Start,
    ) {
        Items(
            isBookmarked,
        ) { actionType ->
            when (actionType) {
                HadithMenuAction.ADD_TO_COLLECTION -> {
                    collectionModalController.show(
                        AddToCollectionRequest(
                            hadithId = item.hadithId,
                        )
                    )
                }

                HadithMenuAction.ADD_TO_BOOKMARK -> {
                    scope.launch {
                        if (!isBookmarked) {
                            viewModel.repo.addUserBookmark(
                                UserBookmark(
                                    hadithId = item.hadithId,
                                    remark = "",
                                )
                            )
                        }

                        withContext(Dispatchers.Main) {
                            bookmarksModalController.show(
                                AddToBookmarkRequest(
                                    hadithId = item.hadithId,
                                )
                            )
                        }
                    }
                }

                HadithMenuAction.COPY_HADITH_NUMBER -> {
                    context.copyToClipboard("${item.collectionName}: ${item.hadithNumber}")

                    MessageUtils.showClipboardMessage(
                        context,
                        text = copiedMessage,
                    )
                }

                else -> {}
            }

            onClose()
        }
    }
}
