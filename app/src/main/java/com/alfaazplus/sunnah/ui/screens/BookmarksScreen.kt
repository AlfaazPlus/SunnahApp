package com.alfaazplus.sunnah.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.library.AddToBookmarksSheet
import com.alfaazplus.sunnah.ui.controllers.rememberModalController
import com.alfaazplus.sunnah.ui.models.userdata.AddToBookmarkRequest
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
private fun BookmarkItemCard(
    bookmark: UserBookmarkNormalized,
    onClick: (UserBookmarkNormalized) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
        onClick = { onClick(bookmark) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row {
                Card(
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        "${bookmark.collectionName}: ${bookmark.item.hadithNumber}",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Text(
                text = bookmark.translationText, style = MaterialTheme.typography.bodyMedium, maxLines = 8, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    userBookmarks: List<UserBookmarkNormalized>,
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
            .padding(16.dp),
    ) {
        items(userBookmarks.size) { index ->
            BookmarkItemCard(
                bookmark = userBookmarks[index],
                onClick = { it ->
                    bookmarksModalController.show(
                        AddToBookmarkRequest(
                            hadithCollectionId = it.item.hadithCollectionId,
                            hadithBookId = it.item.hadithBookId,
                            hadithNumber = it.item.hadithNumber,
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
                            onClick = {
                                showDeleteAllAlert = true
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete), contentDescription = null, tint = MaterialTheme.colorScheme.error
                            )
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