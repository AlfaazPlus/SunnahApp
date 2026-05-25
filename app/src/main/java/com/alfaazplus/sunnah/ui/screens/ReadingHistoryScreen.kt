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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialogAction
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialogActionStyle
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
            containerColor = colorScheme.surfaceContainerLow,
            contentColor = colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
        onClick = { onClick(item) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row {
                NumberingCard(
                    numbering = item.ui.numbering,
                )
            }

            if (!item.ui.translationText.isNullOrEmpty()) {
                Text(
                    text = item.ui.translationText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
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

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        items(
            historyItems.size, key = { historyItems[it].item.hadithId }) { index ->
            ItemCard(
                item = historyItems[index],
                onClick = {
                    val bookId = it.ui.hwc?.bookId ?: return@ItemCard
                    navController.navigate(
                        Routes.READER.args(
                            bookId,
                            it.item.hadithId,
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
                    if (!readHistory.isNullOrEmpty()) {
                        IconButton(
                            onClick = {
                                showDeleteAllAlert = true
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete), contentDescription = null, tint = colorScheme.error
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        if (readHistory == null) {
            Box(
                modifier = Modifier.padding(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                Loader()
            }
        } else {
            Content(
                paddingValues = paddingValues,
                readHistory!!,
            )
        }
    }

    AlertDialog(
        isOpen = showDeleteAllAlert,
        onClose = { showDeleteAllAlert = false },
        title = stringResource(R.string.clear_reading_history),
        actions = listOf(
            AlertDialogAction(
                text = stringResource(R.string.cancel),
            ),
            AlertDialogAction(
                text = stringResource(R.string.delete),
                style = AlertDialogActionStyle.Danger,
                onClick = {
                    scope.launch {
                        viewModel.repo.clearReadHistory()

                        withContext(Dispatchers.Main) {
                            Toast
                                .makeText(context, R.string.reading_history_cleared, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                },
            ),
        ),
        content = {
            Text(
                text = stringResource(R.string.action_cannot_be_undone),
            )
        },
    )
}
