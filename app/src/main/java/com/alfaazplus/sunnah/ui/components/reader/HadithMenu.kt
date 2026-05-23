package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.components.library.AddToCollectionSheet
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.models.userdata.AddToCollectionRequest
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.viewModels.AppViewModel
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class HadithMenuAction {
    ADD_TO_BOOKMARK,
    ADD_TO_COLLECTION,
    SHARE_HADITH,
    REPORT_ISSUE,
    COPY_HADITH_NUMBER,
}

private data class HadithMenuData(
    val hwc: HadithWithContents,
    val cwt: CollectionWithTranslation?,
    val bwt: BookWithTranslation?,
)

@Composable
fun HadithMenu(
    hadithId: String?,
    onClose: () -> Unit,
) {
    // fixme move to ReaderProvider
    val collectionModalController = rememberModalController<AddToCollectionRequest>()
    val bookmarksModalController = rememberModalController<AddToBookmarkRequest>()

    AddToCollectionSheet(collectionModalController)
    AddToBookmarksSheet(bookmarksModalController)

    BottomSheetMenu(
        isOpen = hadithId != null,
        title = stringResource(R.string.hadith_options),
        onDismiss = onClose,
    ) {
        Content(hadithId!!, onClose)
    }
}

@Composable
private fun Content(
    hadithId: String,
    onClose: () -> Unit,
    appVm: AppViewModel = hiltViewModel(),
    viewModel: UserDataViewModel = hiltViewModel(),
) {/*
    val collectionId = cwi.collection.id
    val hadithNumber = hadith.hadith.hadithNumber

    val isBookmarked by viewModel
        .isBookmarked(collectionId, bookId, hadithNumber)
        .collectAsState()
        fixme
*/
    val isBookmarked by viewModel
        .isBookmarked(hadithId)
        .collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val resources = LocalResources.current
    val translationLangCode = ReaderPreferences.observeHadithTranslation()

    val d by produceState<HadithMenuData?>(null, hadithId) {
        val hwc = appVm.repo.dao.getHadithById(hadithId) ?: return@produceState
        val cwt = appVm.repo.dao.getCollectionById(hwc.collectionId)
        val bwt = appVm.repo.dao.getBookById(hwc.bookId)

        value = HadithMenuData(
            hwc, cwt, bwt
        )
    }

    val data = d ?: return Box(
        Modifier.padding(24.dp), contentAlignment = Alignment.Center
    ) {
        Loader()
    }

    Items(
        isBookmarked = isBookmarked
    ) { actionType ->
        when (actionType) {
            /*HadithMenuAction.ADD_TO_COLLECTION -> {
                collectionModalController.show(
                    AddToCollectionRequest(
                        hadithId = hadithId,
                    )
                )
            }

            HadithMenuAction.ADD_TO_BOOKMARK -> {
                scope.launch {
                    if (!isBookmarked) {
                        viewModel.repo.addUserBookmark(
                            UserBookmark(
                                hadithId = hadithId,
                                remark = "",
                            )
                        )
                    }

                    withContext(Dispatchers.Main) {
                        bookmarksModalController.show(
                            AddToBookmarkRequest(
                                hadithId = hadithId,
                            )
                        )
                    }
                }
            }*/

            HadithMenuAction.SHARE_HADITH -> {
                HadithHelper.shareHadith(
                    context,
                    hwc = data.hwc,
                    collectionName = data.cwt?.getTitlePair(translationLangCode),
                    bookName = data.bwt?.getTitlePair(translationLangCode),
                    translationLangCode = translationLangCode,
                )
            }

            HadithMenuAction.REPORT_ISSUE -> {
                context.copyToClipboard(data.hwc.hadithId)
                MessageUtils.showClipboardMessage(context, resources.getString(R.string.paste_reference_github_issue))
                NavigationHelper.openGithubIssuesHadithReport(context)
            }

            else -> {
                // noop
            }
        }

        onClose()
    }
}

@Composable
private fun Items(
    isBookmarked: Boolean,
    onItemClick: (HadithMenuAction) -> Unit,
) {
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
                    onItemClick(HadithMenuAction.ADD_TO_BOOKMARK)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.add_to_collection),
                icon = R.drawable.ic_library,
                onClick = {
                    onItemClick(HadithMenuAction.ADD_TO_COLLECTION)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.share_this_hadith),
                icon = R.drawable.ic_share,
                onClick = {
                    onItemClick(HadithMenuAction.SHARE_HADITH)
                },
            )
        }
        item {
            Item(
                text = stringResource(R.string.report_issue),
                icon = R.drawable.ic_flag,
                onClick = {
                    onItemClick(HadithMenuAction.REPORT_ISSUE)
                },
            )
        }
    }
}

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
