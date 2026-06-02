package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.helpers.HadithGradeText
import com.alfaazplus.sunnah.ui.components.NoQuranAppAlert
import com.alfaazplus.sunnah.ui.components.library.BookmarkViewerData
import com.alfaazplus.sunnah.ui.components.library.BookmarkViewerSheet
import com.alfaazplus.sunnah.ui.components.library.AddToCollectionSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.HadithGradeInfoSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.HadithReferenceSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.NarratorsChainSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.QuickReference
import com.alfaazplus.sunnah.ui.components.reader.dialogs.QuickReferenceData
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.models.QuranReference

data class HadithActions(
    val onHadithOption: (hadithId: String) -> Unit,
    val onAddToCollectionRequest: (hadithId: String) -> Unit,
    val onBookmarkViewerRequest: (BookmarkViewerData) -> Unit,
    val onNumberReferenceRequest: (hadithId: String) -> Unit,
    val onQuickReferenceRequest: (hadithId: String) -> Unit,
    val onQuranReferenceRequest: (QuranReference) -> Unit,
    val showGradeInfo: (gradeText: HadithGradeText) -> Unit,
    val showNarratorsChain: (hadithId: String) -> Unit,
)

val LocalHadithActions = staticCompositionLocalOf<HadithActions> {
    error("LocalHadithActions not provided")
}

@Composable
fun ActionsProvider(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    var showNoQuranAppBottomSheet by rememberSaveable { mutableStateOf(false) }
    var activeNumberReferenceHadithId by rememberSaveable { mutableStateOf<String?>(null) }
    var activeHadithMenuId by rememberSaveable { mutableStateOf<String?>(null) }
    var activeGradeInfo by rememberSaveable { mutableStateOf<HadithGradeText?>(null) }
    var activeNarratorsChainHadithId by rememberSaveable { mutableStateOf<String?>(null) }
    var activeQuickReference by remember { mutableStateOf<QuickReferenceData?>(null) }
    var addToCollectionRequest by remember { mutableStateOf<String?>(null) }
    var bookmarkViewerData by remember { mutableStateOf<BookmarkViewerData?>(null) }

    CompositionLocalProvider(
        LocalHadithActions provides HadithActions(
            onHadithOption = {
                activeHadithMenuId = it
            },
            onAddToCollectionRequest = {
                addToCollectionRequest = it
            },
            onBookmarkViewerRequest = {
                bookmarkViewerData = it
            },
            onNumberReferenceRequest = {
                activeNumberReferenceHadithId = it
            },
            onQuickReferenceRequest = { id ->
                activeQuickReference = QuickReferenceData(hadithIds = listOf(id))
            },
            onQuranReferenceRequest = { reference ->
                try {
                    NavigationHelper.openQuranReference(context, reference)
                } catch (e: Exception) {
                    Logger.e(e)
                    showNoQuranAppBottomSheet = true
                }
            },
            showGradeInfo = {
                activeGradeInfo = it
            },
            showNarratorsChain = {
                activeNarratorsChainHadithId = it
            },
        )
    ) {
        content()

        HadithReferenceSheet(activeNumberReferenceHadithId) {
            activeNumberReferenceHadithId = null
        }

        HadithGradeInfoSheet(activeGradeInfo) {
            activeGradeInfo = null
        }

        NarratorsChainSheet(activeNarratorsChainHadithId) {
            activeNarratorsChainHadithId = null
        }

        HadithMenu(
            hadithId = activeHadithMenuId,
            onClose = {
                activeHadithMenuId = null
            },
        )

    }

    NoQuranAppAlert(showNoQuranAppBottomSheet) {
        showNoQuranAppBottomSheet = false
    }

    QuickReference(activeQuickReference) {
        activeQuickReference = null
    }

    BookmarkViewerSheet(bookmarkViewerData) {
        bookmarkViewerData = null
    }

    AddToCollectionSheet(addToCollectionRequest) {
        addToCollectionRequest = null
    }
}
