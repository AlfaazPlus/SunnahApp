package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.ReadHistory
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.Section
import com.alfaazplus.sunnah.ui.components.common.SectionEmptyMessage
import com.alfaazplus.sunnah.ui.components.common.SectionHeaderActionButton
import com.alfaazplus.sunnah.ui.components.common.SectionHeaderViewAll
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.components.library.CreateUpdateCollectionSheet
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.HadithRepoViewModel
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel

@Composable
private fun ReadHistoryItemCard(
    item: ReadHistory,
    onClick: () -> Unit,
    hadithViewModel: HadithRepoViewModel = hiltViewModel(),
) {
    val hadithCollection = produceState<CollectionWithInfo?>(null) {
        value = hadithViewModel.repo.getCollection(item.hadithCollectionId)
    }.value

    val hadithBook = produceState<BookWithInfo?>(null) {
        value = hadithViewModel.repo.getBookById(item.hadithCollectionId, item.hadithBookId)
    }.value

    Box(
        modifier = Modifier
            .width(250.dp)
            .height(70.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onClick()
            }
            .padding(12.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${hadithCollection?.info?.name} : ${item.hadithNumber}",
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = "Book: ${hadithBook?.info?.title}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.alpha(0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 6.dp)
            )
        }
    }
}

@Composable
private fun SectionReadHistory(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val readHistory by viewModel.recentReadHistory.collectAsState()
    val navController = LocalNavHostController.current

    Section(
        icon = R.drawable.ic_history,
        title = stringResource(R.string.reading_history),
        headerRightContent = {
            SectionHeaderViewAll {
                navController.navigate(Routes.READING_HISTORY)
            }
        },
    ) {
        if (readHistory.isEmpty()) {
            SectionEmptyMessage(
                stringResource(R.string.no_reading_history),
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(
                    readHistory.size, key = { readHistory[it].key() }) { index ->
                    val item = readHistory[index]

                    ReadHistoryItemCard(
                        item,
                        onClick = {
                            navController.navigate(
                                Routes.READER.args(
                                    item.hadithCollectionId,
                                    item.hadithBookId,
                                    item.hadithNumber,
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}


@Composable
private fun UserBookmarkCard(
    bookmark: UserBookmark,
    onClick: () -> Unit,
    hadithViewModel: HadithRepoViewModel = hiltViewModel(),
) {

    val hadithCollection = produceState<CollectionWithInfo?>(null) {
        value = hadithViewModel.repo.getCollection(bookmark.hadithCollectionId)
    }.value

    val hadithBook = produceState<BookWithInfo?>(null) {
        value = hadithViewModel.repo.getBookById(bookmark.hadithCollectionId, bookmark.hadithBookId)
    }.value

    Box(
        modifier = Modifier
            .width(250.dp)
            .height(90.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onClick()
            }
            .padding(12.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${hadithCollection?.info?.name} : ${bookmark.hadithNumber}",
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = "Book: ${hadithBook?.info?.title}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.alpha(0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        if (bookmark.remark.isNotBlank()) {
            Icon(
                painter = painterResource(R.drawable.ic_hadith_text_option),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp),
            )
        }
    }
}

@Composable
private fun SectionBookmarks(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val userBookmarks by viewModel.recentUserBookmarks.collectAsState()
    val navController = LocalNavHostController.current

    val bookmarksModalController = rememberModalController<AddToBookmarkRequest>()

    AddToBookmarksSheet(bookmarksModalController)

    Section(
        icon = R.drawable.ic_bookmark,
        title = stringResource(R.string.bookmarks),
        headerRightContent = {
            SectionHeaderViewAll {
                navController.navigate(Routes.BOOKMARKS)
            }
        },
    ) {
        if (userBookmarks.isEmpty()) {
            SectionEmptyMessage(
                stringResource(R.string.no_bookmarks)
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(userBookmarks.size) { index ->
                    val bookmark = userBookmarks[index]

                    UserBookmarkCard(
                        bookmark,
                        onClick = {
                            bookmarksModalController.show(
                                AddToBookmarkRequest(
                                    hadithCollectionId = bookmark.hadithCollectionId,
                                    hadithBookId = bookmark.hadithBookId,
                                    hadithNumber = bookmark.hadithNumber,
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun UserCollectionCard(collection: UserCollection, onClick: (UserCollection) -> Unit) {
    val userColor = collection.color?.let { Color(it.toColorInt()) } ?: Color.Gray
    val itemsCount = collection.itemsCount.collectAsState(0).value

    val gradientColors = listOf(
        Color.Transparent,
        userColor,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f),
                ),
                shape = MaterialTheme.shapes.medium,
            )
            .border(
                width = 1.dp,
                color = userColor.alpha(0.1f),
                shape = MaterialTheme.shapes.medium,
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
                text = "$itemsCount items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.alpha(0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }

}

@Composable
private fun SectionCollections(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var showCreateCollectionSheet by remember { mutableStateOf(false) }
    val userCollections by viewModel.userCollections.collectAsState()
    val navController = LocalNavHostController.current

    Section(
        icon = R.drawable.ic_library,
        title = stringResource(R.string.collections),
        headerRightContent = {
            if (userCollections.isNotEmpty()) {
                SectionHeaderActionButton(
                    icon = R.drawable.ic_add,
                    text = stringResource(R.string.label_new),
                ) {
                    showCreateCollectionSheet = true
                }
            }
        },
    ) {
        if (userCollections.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.alpha(0.6f), MaterialTheme.shapes.medium)
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.no_collections),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                OutlinedButton(
                    onClick = {
                        showCreateCollectionSheet = true
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.create_collection),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                userScrollEnabled = false,
                columns = GridCells.Fixed(integerResource(R.integer.collection_grid_columns_2)),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp),
                modifier = Modifier.heightIn(max = 1000.dp)
            ) {
                items(userCollections.size) {
                    UserCollectionCard(userCollections[it]) { collection ->
                        navController.navigate(Routes.SINGLE_COLLECTION.args(collection.id, collection.name))
                    }
                }
            }
        }
    }

    CreateUpdateCollectionSheet(
        showCreateCollectionSheet,
        onClose = {
            showCreateCollectionSheet = false
        },
    )
}

@Composable
fun LibraryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionReadHistory()
        SectionBookmarks()
        SectionCollections()
    }
}