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
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.models.userdata.ReadHistoryNormalized
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
private fun ItemCard(
    item: ReadHistoryNormalized,
    onClick: (ReadHistoryNormalized) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
        onClick = { onClick(item) },
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
                        "${item.collectionName}: ${item.item.hadithNumber}",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Text(
                text = item.translationText, style = MaterialTheme.typography.bodyMedium, maxLines = 8, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    historyItems: List<ReadHistoryNormalized>,
) {
    val navController = LocalNavHostController.current

    if (historyItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.no_reading_history),
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
        items(
            historyItems.size, key = { historyItems[it].key() }) { index ->
            ItemCard(
                item = historyItems[index],
                onClick = { it ->
                    navController.navigate(
                        Routes.READER.args(
                            it.item.hadithCollectionId,
                            it.item.hadithBookId,
                            it.item.hadithNumber,
                        )
                    )
                },
            )
        }
    }
}

@Composable
fun ReadingHistoryScreen(
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var showDeleteAllAlert by remember { mutableStateOf(false) }
    val readHistory by viewModel.allReadHistory.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.reading_history),
                actions = {
                    if (readHistory.isNotEmpty()) {
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
            readHistory,
        )
    }

    AlertDialog(
        isOpen = showDeleteAllAlert,
        onClose = { showDeleteAllAlert = false },
        title = stringResource(R.string.clear_reading_history),
        cancelText = stringResource(R.string.cancel),
        confirmText = stringResource(R.string.delete),
        confirmColors = MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError,
        onConfirm = {
            scope.launch {
                viewModel.repo.clearReadHistory()

                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(context, R.string.reading_history_cleared, Toast.LENGTH_LONG)
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