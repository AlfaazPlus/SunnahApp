package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.api.DownloadSourceUtils
import com.alfaazplus.sunnah.ui.components.common.AlertCard
import com.alfaazplus.sunnah.ui.components.common.AlertType
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import kotlinx.coroutines.launch

@Composable
fun ResourceDownloadSrcSheet(isOpen: Boolean, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val currentDownloadSrc = DownloadSourceUtils.observeResourceDownloadSrc()

    val items = listOf(
        Pair(DownloadSourceUtils.DOWNLOAD_SRC_ALFAAZ_PLUS, "AlfaazPlus"),
        Pair(DownloadSourceUtils.DOWNLOAD_SRC_GITHUB, "GitHub Raw"),
        Pair(DownloadSourceUtils.DOWNLOAD_SRC_JSDELIVR, "JsDelivr"),
    )

    BottomSheet(
        isOpen = isOpen,
        onDismiss = onDismiss,
        icon = R.drawable.ic_download,
        title = stringResource(R.string.resource_download_source),
    ) {
        AlertCard(
            modifier = Modifier.padding(horizontal = 12.dp),
        ) {
            Text(
                stringResource(R.string.resource_download_source_desc),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            items.forEach { (downloadSrc, title) ->
                RadioItem(
                    titleStr = title,
                    subtitleStr = DownloadSourceUtils.getDownloadSourceName(downloadSrc),
                    selected = currentDownloadSrc == downloadSrc,
                    onClick = {
                        coroutineScope.launch {
                            DownloadSourceUtils.setResourceDownloadSrc(downloadSrc)
                        }

                        onDismiss()
                    },
                )
            }
        }
    }
}