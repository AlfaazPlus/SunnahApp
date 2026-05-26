package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch


private fun getPreviousBook(books: List<BookWithTranslation>, currentBookId: String?): BookWithTranslation? {
    val currentIndex = books.indexOfFirst { it.book.id == currentBookId }
    val previousIndex = currentIndex - 1

    if (previousIndex < 0) return null

    return books[previousIndex]
}

private fun getNextBook(books: List<BookWithTranslation>, currentBookId: String?): BookWithTranslation? {
    val currentIndex = books.indexOfFirst { it.book.id == currentBookId }
    val nextIndex = currentIndex + 1

    if (nextIndex >= books.lastIndex) return null

    return books[nextIndex]
}

@Composable
fun VerticalReaderFooter(
    readerVm: ReaderViewModel,
    onTopClick: () -> Unit,
) {
    val books by readerVm.books.collectAsStateWithLifecycle()
    val hasMultipleBooks = books.size > 1

    if (!hasMultipleBooks) {
        return
    }

    val isDarkTheme = ThemeUtils.observeDarkTheme()

    val containerColor = if (isDarkTheme) colorScheme.surfaceContainer else colorScheme.background
    val contentColor = if (isDarkTheme) colorScheme.onSurface else colorScheme.onBackground

    val scope = rememberCoroutineScope()

    val currentBookId by readerVm.activeBookId.collectAsStateWithLifecycle()

    val prevBook = getPreviousBook(books, currentBookId)
    val nextBook = getNextBook(books, currentBookId)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        NavigationButton(
            bookNumber = prevBook?.book?.number,
            isPrevious = true,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            scope.launch {
                prevBook?.let {
                    readerVm.initReader(it.book.id)
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small)
                .background(containerColor)
                .clickable { onTopClick() }) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.ic_arrow_top),
                contentDescription = stringResource(R.string.settings),
                tint = contentColor,
            )
            Text(
                text = stringResource(R.string.top),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
        }

        NavigationButton(
            bookNumber = nextBook?.book?.number,
            isPrevious = false,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            scope.launch {
                nextBook?.let {
                    readerVm.initReader(it.book.id)
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavigationButton(
    bookNumber: String?,
    isPrevious: Boolean,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .padding(horizontal = 10.dp)
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .clickable(
                enabled = bookNumber != null,
            ) { onClick() }
            .alpha(if (bookNumber != null) 1f else 0.5f),
    ) {

        if (bookNumber != null) {
            Text(
                text = "Book $bookNumber",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isPrevious) {
                ReaderNavigationButtonIcon(
                    icon = R.drawable.ic_arrow_left,
                    label = stringResource(R.string.previousBook),
                )
            }

            Text(
                text = if (isPrevious) stringResource(R.string.previousBook) else stringResource(R.string.nextBook),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Normal,
            )

            if (!isPrevious) {
                ReaderNavigationButtonIcon(
                    icon = R.drawable.ic_arrow_right,
                    label = stringResource(R.string.nextBook),
                )
            }
        }
    }
}
