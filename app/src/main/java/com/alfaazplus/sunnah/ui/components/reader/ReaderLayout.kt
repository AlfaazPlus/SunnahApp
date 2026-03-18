package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel

@Composable
fun ReaderLayout(
    readerVm: ReaderViewModel,
    currentHadithNumber: () -> String?,
    onJumpToBook: (BookWithInfo) -> Unit,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
    content: @Composable () -> Unit,
) {
    val bookId = readerVm.bookId
    val books = readerVm.books.value!!
    val hadiths = readerVm.hadithList

    Row {
        ReaderHadithNavigator(
            modifier = Modifier.widthIn(
                max = 260.dp
            ),
            isInBottomSheet = false,
            books = books,
            hadiths = hadiths,
            currentBookId = bookId.value ?: 0,
            currentHadithNumber = currentHadithNumber() ?: "",
            currentNavigatorTab = readerVm.currentNavigatorTab,
            onChangeNavigatorTab = { readerVm.currentNavigatorTab = it },
            onJumpToBook = { bwi ->
                onJumpToBook(bwi)
            },
            onJumpToHadith = { hwt ->
                onJumpToHadith(hwt)
            },
        )

        VerticalDivider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            content()
        }
    }
}