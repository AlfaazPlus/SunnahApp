package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.helpers.HadithGradeText
import com.alfaazplus.sunnah.ui.components.NoQuranAppAlert
import com.alfaazplus.sunnah.ui.components.reader.dialogs.HadithGradeInfoSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.HadithReferenceSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.NarratorsChainSheet
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.models.QuranReference

data class HadithActions(
    val onHadithOption: (hadithId: String) -> Unit,
    val onNumberReferenceRequest: (hadithId: String) -> Unit,
    val onQuickReferenceRequest: (hadithId: String) -> Unit,
    val onQuranReferenceRequest: (chapterNo: Int, verses: String) -> Unit,
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

    CompositionLocalProvider(
        LocalHadithActions provides HadithActions(
            onHadithOption = {
                activeHadithMenuId = it
            },
            onNumberReferenceRequest = {
                activeNumberReferenceHadithId = it
            },
            onQuickReferenceRequest = {},
            onQuranReferenceRequest = { chapterNo, verses ->
                val verses = verses.split("-")
                val fromVerse = verses[0].toInt()
                var toVerse = fromVerse

                if (verses.size > 1) {
                    toVerse = verses[1].toInt()
                }

                try {
                    NavigationHelper.openQuranReference(context, QuranReference(chapterNo, fromVerse, toVerse))
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
}
