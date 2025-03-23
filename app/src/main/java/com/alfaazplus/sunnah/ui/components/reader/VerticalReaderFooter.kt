package com.alfaazplus.sunnah.ui.components.reader

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel


private fun getPreviousBook(books: List<BookWithInfo>, currentBookId: Int?): BookWithInfo? {
    val currentOrder = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection ?: return null
    val previousOrder = currentOrder.minus(1)
    val previousBook = books.firstOrNull { it.book.orderInCollection == previousOrder }

    return previousBook
}

private fun getNextBook(books: List<BookWithInfo>, currentBookId: Int?): BookWithInfo? {
    val currentOrder = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection ?: return null
    val nextOrder = currentOrder.plus(1)
    val nextBook = books.firstOrNull { it.book.orderInCollection == nextOrder }

    return nextBook
}


@Composable
private fun NavigationButtonIcon(
    @DrawableRes
    icon: Int,
    label: String,
    modifier: Modifier,
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = label,
        modifier = modifier
            .width(14.dp)
            .height(14.dp),
        tint = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RowScope.NavigationButton(
    bookId: Int?,
    isPrevious: Boolean,
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
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                enabled = bookId != null,
            ) { onClick() }
            .alpha(if (bookId != null) 1f else 0.5f)
    ) {
        if (bookId != null) {
            Text(
                text = "Book $bookId",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isPrevious) {
                NavigationButtonIcon(
                    icon = R.drawable.ic_arrow_left,
                    label = stringResource(R.string.previousBook),
                    modifier = Modifier
                        .padding(end = 4.dp)
                )
            }

            Text(
                text = if (isPrevious) stringResource(R.string.previousBook) else stringResource(R.string.nextBook),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal,
            )

            if (!isPrevious) {
                NavigationButtonIcon(
                    icon = R.drawable.ic_arrow_right,
                    label = stringResource(R.string.nextBook),
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VerticalReaderFooter(
    vm: ReaderViewModel,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onTopClick: () -> Unit,
) {
    val prevBook = getPreviousBook(vm.books.value!!, vm.bookId.value)
    val nextBook = getNextBook(vm.books.value!!, vm.bookId.value)

    Row {
        NavigationButton(
            bookId = prevBook?.book?.id,
            isPrevious = true,
        ) { onPreviousClick() }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onTopClick() }
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.ic_arrow_top),
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Top",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
        }

        NavigationButton(
            bookId = nextBook?.book?.id,
            isPrevious = false,
        ) { onNextClick() }
    }
}