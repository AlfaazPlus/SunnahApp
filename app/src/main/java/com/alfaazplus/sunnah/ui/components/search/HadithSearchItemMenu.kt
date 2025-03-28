package com.alfaazplus.sunnah.ui.components.search

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenuItem
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils


@Composable
private fun Items(
    item: HadithSearchResult,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val copiedMessage = stringResource(R.string.copied_to_clipboard)

    BottomSheetMenuItem(
        text = stringResource(R.string.copy_hadith_number),
        icon = R.drawable.ic_clipboard,
    ) {
        clipboardManager.setClip(ClipEntry(ClipData.newPlainText("", "${item.collectionName}: ${item.hadith.hadithNumber}")))
        MessageUtils.showClipboardMessage(
            context,
            text = copiedMessage,
        )
        onClose()
    }
    BottomSheetMenuItem(
        text = stringResource(R.string.add_to_bookmarks),
        icon = R.drawable.ic_bookmark_plus,
    ) {
        onClose() // todo:
    }
    BottomSheetMenuItem(
        text = stringResource(R.string.add_to_collection),
        icon = R.drawable.ic_library,
    ) {
        onClose() // todo:
    }
}

@Composable
fun HadithSearchItemMenu(
    item: HadithSearchResult,
    isOpen: Boolean,
    onClose: () -> Unit,
) {
    BottomSheetMenu(
        title = "${item.collectionName}: ${item.hadith.hadithNumber}",
        isOpen = isOpen,
        onDismiss = onClose,
        headerArrangement = Arrangement.Start,
    ) {
        Items(item, onClose)
    }
}