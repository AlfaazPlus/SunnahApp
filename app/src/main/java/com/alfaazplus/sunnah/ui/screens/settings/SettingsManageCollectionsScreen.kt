package com.alfaazplus.sunnah.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkInfo
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.viewModels.CollectionListViewModel
import com.alfaazplus.sunnah.ui.viewModels.DownloadCollectionViewModel

@Composable
fun ManageHadithCollectionItem(
    cwi: CollectionWithInfo,
    vm: CollectionListViewModel,
    downloadVm: DownloadCollectionViewModel,
    setLastDownloadError: (String?) -> Unit,
) {
    val collectionId = cwi.collection.id
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    val _info by downloadVm
        .getDownloadStateFlow(collectionId)
        .collectAsState(null)
    val workInfo = _info

    val isDownloaded = cwi.isDownloaded == true
    val isDownloading = workInfo?.state?.isFinished != null && !workInfo.state.isFinished

    LaunchedEffect(workInfo) {
        if (workInfo == null || !workInfo.state.isFinished) {
            return@LaunchedEffect
        }

        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
            MessageUtils.showToast(context, "Downloaded: ${cwi.collection.name} (${cwi.info?.name})", Toast.LENGTH_SHORT)
        } else if (workInfo.state == WorkInfo.State.FAILED) {
            MessageUtils.showToast(context, "Failed to download: ${cwi.collection.name} (${cwi.info?.name})", Toast.LENGTH_SHORT)
            setLastDownloadError(workInfo.outputData.getString("error"))
        }
    }

    val downloadCollection = {
        val collectionId = collectionId

        downloadVm.startDownload(collectionId)
    }

    val deleteCollection = {
        vm.deleteCollection(collectionId) {
            MessageUtils.showToast(context, "Deleted: ${cwi.collection.name} (${cwi.info?.name})", Toast.LENGTH_SHORT)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(enabled = !isDownloading) {
                if (isDownloaded) {
                    showDeleteDialog = true
                } else if (!isDownloading) {
                    downloadCollection()
                }
            }
            .alpha(if (isDownloading) 0.5f else 1f)
            .padding(horizontal = 22.dp, vertical = 10.dp)) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = cwi.collection.name, style = MaterialTheme.typography.titleMedium, fontFamily = fontUthmani, fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = cwi.info?.name ?: "",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 3.dp)
            )
        }

        if (isDownloading) {
            Loader(size = 24.dp)
        } else {
            Icon(
                painter = painterResource(if (isDownloaded) R.drawable.ic_delete else R.drawable.ic_download),
                contentDescription = null,
                tint = if (isDownloaded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
    }

    AlertDialog(
        isOpen = showDeleteDialog,
        onClose = { showDeleteDialog = false },
        title = stringResource(R.string.delete_collection),
        cancelText = stringResource(R.string.cancel),
        confirmText = stringResource(R.string.delete),
        confirmColors = MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError,
        onConfirm = deleteCollection,
        content = {
            Text(
                text = stringResource(R.string.msg_delete_collection) + "\n ${cwi.collection.name} (${cwi.info?.name})",
                style = MaterialTheme.typography.bodyMedium
            )
        },
    )
}

@Composable
fun SettingsManageCollectionsScreen(
    vm: CollectionListViewModel = hiltViewModel(),
    downloadVm: DownloadCollectionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val collections by vm.collections.collectAsState()
    var lastDownloadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.loadCollections()
    }

    Scaffold(topBar = { AppBar(title = stringResource(R.string.manage_collections)) }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues), contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            items(collections.size, key = { collections[it].collection.id }) {
                val cwi = collections[it]
                ManageHadithCollectionItem(cwi, vm, downloadVm) {
                    lastDownloadError = it
                }
            }
        }

        AlertDialog(
            isOpen = lastDownloadError != null,
            onClose = { lastDownloadError = null },
            title = "Download Error",
            cancelText = stringResource(R.string.cancel),
            confirmText = "Copy",
            onConfirm = {
                context.copyToClipboard(lastDownloadError ?: "")
            },
            content = {
                Column(
                    modifier = Modifier
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = lastDownloadError ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
        )
    }
}