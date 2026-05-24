package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.library.BookmarkViewerData
import com.alfaazplus.sunnah.ui.components.library.BookmarkViewerSheet
import com.alfaazplus.sunnah.ui.components.library.CreateUpdateCollectionSheet
import com.alfaazplus.sunnah.ui.components.library.SectionEmptyMessage
import com.alfaazplus.sunnah.ui.components.library.SectionHeaderActionButton
import com.alfaazplus.sunnah.ui.components.library.SectionHeaderViewAll
import com.alfaazplus.sunnah.ui.models.userdata.ReadHistoryNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel

@Composable
fun LibraryScreen(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val userCollections by viewModel.userCollections.collectAsState()
    var showCreateCollectionSheet by remember { mutableStateOf(false) }
    val navController = LocalNavHostController.current

    CreateUpdateCollectionSheet(
        showCreateCollectionSheet,
        onClose = { showCreateCollectionSheet = false },
    )

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.library),
                showNavigationIcon = false,
            )
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 20.dp,
                bottom = 150.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    iconRes = R.drawable.ic_history,
                    title = stringResource(R.string.reading_history),
                    headerRightContent = {
                        SectionHeaderViewAll {
                            navController.navigate(Routes.READING_HISTORY)
                        }
                    },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionReadHistory()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    modifier = Modifier.padding(top = 16.dp),
                    iconRes = R.drawable.ic_bookmark,
                    title = stringResource(R.string.bookmarks),
                    headerRightContent = {
                        SectionHeaderViewAll {
                            navController.navigate(Routes.BOOKMARKS)
                        }
                    },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionBookmarks()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    modifier = Modifier.padding(top = 16.dp),
                    iconRes = R.drawable.ic_library,
                    title = stringResource(R.string.collections),
                    headerRightContent = {
                        if (userCollections.isNotEmpty()) {
                            SectionHeaderActionButton(
                                icon = R.drawable.ic_add,
                                text = stringResource(R.string.label_new),
                            ) { showCreateCollectionSheet = true }
                        }
                    },
                )
            }

            if (userCollections.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CollectionsEmptyState(
                        onCreateCollection = { showCreateCollectionSheet = true },
                    )
                }
            } else {
                items(
                    items = userCollections,
                    key = { it.id },
                ) { collection ->
                    UserCollectionCard(collection) {
                        navController.navigate(
                            Routes.SINGLE_COLLECTION.args(collection.id, collection.name),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionReadHistory(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val readHistory by viewModel.recentReadHistory.collectAsState()
    val navController = LocalNavHostController.current

    if (readHistory.isEmpty()) {
        SectionEmptyMessage(
            message = stringResource(R.string.no_reading_history),
            horizontalPadding = 0.dp,
        )
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                readHistory.size, key = { readHistory[it].item.hadithId }) { index ->
                val item = readHistory[index]

                ReadHistoryItemCard(
                    history = item,
                    onNavigate = { bookId, hadithId ->
                        navController.navigate(
                            Routes.READER.args(bookId, hadithId),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun ReadHistoryItemCard(
    history: ReadHistoryNormalized,
    onNavigate: (bookId: String, hadithId: String) -> Unit,
) {
    val hadithId = history.item.hadithId

    Surface(
        shape = shapes.medium,
        color = colorScheme.surfaceContainer,
        onClick = {
            val bookId = history.ui.hwc?.bookId ?: return@Surface
            onNavigate(bookId, hadithId)
        },

        ) {
        Column(
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = history.ui.visibleNumbering,
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = history.ui.bookTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant.alpha(0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 6.dp)
            )
        }
    }
}

@Composable
private fun SectionBookmarks(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val userBookmarks by viewModel.recentUserBookmarks.collectAsState()
    var bookmarkViewerData by remember { mutableStateOf<BookmarkViewerData?>(null) }

    BookmarkViewerSheet(bookmarkViewerData) {
        bookmarkViewerData = null
    }

    if (userBookmarks.isEmpty()) {
        SectionEmptyMessage(
            message = stringResource(R.string.no_bookmarks),
            horizontalPadding = 0.dp,
        )
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(userBookmarks.size) { index ->
                val bookmark = userBookmarks[index]

                UserBookmarkCard(
                    bookmark,
                    onClick = {
                        bookmarkViewerData = BookmarkViewerData(
                            hadithId = bookmark.item.hadithId,
                            openInReader = true,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun UserBookmarkCard(
    bookmark: UserBookmarkNormalized,
    onClick: () -> Unit,
) {
    Surface(
        shape = shapes.medium,
        color = colorScheme.surfaceContainer,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .width(250.dp)
                .height(90.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = bookmark.ui.visibleNumbering,
                style = MaterialTheme.typography.titleSmall,
            )

            if (bookmark.item.remark.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        appendInlineContent("user_note", "[icon]")
                        append(" ")
                        append(bookmark.item.remark)
                    },
                    inlineContent = mapOf(
                        "user_note" to InlineTextContent(
                            Placeholder(
                                width = 16.sp,
                                height = 16.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.pencil_line),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant.alpha(0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 6.dp),
                )
            } else {
                Text(
                    text = bookmark.ui.bookTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant.alpha(0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

        }
    }
}


@Composable
fun UserCollectionCard(collection: UserCollection, onClick: (UserCollection) -> Unit) {
    val userColor = collection.color?.let { Color(it.toColorInt()) } ?: Color.Gray

    val gradientColors = listOf(
        Color.Transparent,
        userColor,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shapes.medium)
            .background(
                color = colorScheme.surface,
            )
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f),
                ),
                shape = shapes.medium,
            )
            .border(
                width = 1.dp,
                color = userColor.alpha(0.1f),
                shape = shapes.medium,
            )
            .clickable { onClick(collection) },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .fillMaxWidth(0.20f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(userColor),
            ) {}

            Text(
                text = collection.name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "${collection.itemsCount} items",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant.alpha(0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }

}

@Composable
fun CollectionsEmptyState(
    onCreateCollection: () -> Unit,
) {
    Column(
        modifier = Modifier
            .border(1.dp, colorScheme.outlineVariant.alpha(0.6f), shapes.medium)
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.no_collections),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        OutlinedButton(
            onClick = onCreateCollection,
            border = BorderStroke(
                width = 1.dp,
                color = colorScheme.primary,
            ),
        ) {
            Text(
                text = stringResource(R.string.create_collection),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun SectionTitle(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    headerRightContent: (@Composable () -> Unit)?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = title,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f),
        )

        headerRightContent?.let {
            it()
        }
    }
}
