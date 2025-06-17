package com.alfaazplus.sunnah.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.IconButton
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.HadithRepoViewModel
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
private fun BookmarkItemCard(
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
            .fillMaxWidth()
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
private fun Content(
    paddingValues: PaddingValues,
    userBookmarks: List<UserBookmark>,
) {
    val bookmarksModalController = rememberModalController<AddToBookmarkRequest>()
    AddToBookmarksSheet(bookmarksModalController)

    if (userBookmarks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.no_bookmarks),
            )
        }

        return
    }

    LazyColumn(
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        items(userBookmarks.size) { index ->
            val bookmark = userBookmarks[index]
            BookmarkItemCard(
                bookmark = bookmark,
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

@Composable
fun BookmarksScreen(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var showDeleteAllAlert by remember { mutableStateOf(false) }
    val userBookmarks by viewModel.allUserBookmarks.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.bookmarks),
                actions = {
                    if (userBookmarks.isNotEmpty()) {
                        IconButton(
                            painter = painterResource(R.drawable.ic_delete),
                            small = true,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                        ) {
                            showDeleteAllAlert = true
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Content(
            paddingValues = paddingValues,
            userBookmarks,
        )
    }

    AlertDialog(
        isOpen = showDeleteAllAlert,
        onClose = { showDeleteAllAlert = false },
        title = stringResource(R.string.delete_all_bookmarks),
        cancelText = stringResource(R.string.cancel),
        confirmText = stringResource(R.string.delete),
        confirmColors = MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError,
        onConfirm = {
            scope.launch {
                viewModel.repo.clearUserBookmarks()

                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(context, R.string.all_bookmarks_deleted, Toast.LENGTH_LONG)
                        .show()
                }
            }
        },
        content = {
            Text(
                text = stringResource(R.string.action_cannot_be_undone),
            )
        },
    )
}