package com.alfaazplus.sunnah.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialogAction
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialogActionStyle
import com.alfaazplus.sunnah.ui.utils.app.LocalAppLocale
import com.alfaazplus.sunnah.ui.utils.managers.ResourceDownloadStatus
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import com.alfaazplus.sunnah.ui.viewModels.TranslationDownloadUiState
import com.alfaazplus.sunnah.ui.viewModels.TranslationDownloadViewModel
import com.alfaazplus.sunnah.ui.viewModels.TranslationUiModel
import com.alfaazplus.sunnah.ui.components.common.IconButton as AppIconButton

@Composable
fun SettingsTranslationsScreen(
    downloadVm: TranslationDownloadViewModel = hiltViewModel(),
) {
    val uiState by downloadVm.uiState.collectAsStateWithLifecycle()
    var deleteDialogData by remember { mutableStateOf<TranslationUiModel?>(null) }
    val downloadStates = uiState.downloadStates

    val isAnyDownloading = downloadStates.values.any {
        it is ResourceDownloadStatus.Started || it is ResourceDownloadStatus.InProgress
    }

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.selectTranslation)
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(top = 16.dp, bottom = 64.dp),
        ) {
            uiState.rows.forEach { row ->
                val status = downloadStates[row.id] ?: ResourceDownloadStatus.Idle

                ItemRow(
                    row, status, downloadVm, uiState, isAnyDownloading,
                    onDeleteRequest = { deleteDialogData = row },
                )
            }
        }
    }


    AlertDialog(
        isOpen = deleteDialogData != null,
        onClose = { deleteDialogData = null },
        title = stringResource(R.string.deleteTranslation),
        actions = listOf(
            AlertDialogAction(
                text = stringResource(R.string.cancel)
            ),
            AlertDialogAction(
                text = stringResource(R.string.delete),
                style = AlertDialogActionStyle.Danger,
                dismissOnClick = false,
                onClick = {
                    if (deleteDialogData != null) {
                        downloadVm.deleteTranslation(deleteDialogData!!.id)
                    }

                    deleteDialogData = null
                },
            ),
        ),
    ) {
        Text(deleteDialogData?.title ?: "")
    }
}

@Composable
private fun ItemRow(
    row: TranslationUiModel,
    downloadStatus: ResourceDownloadStatus,
    downloadVm: TranslationDownloadViewModel,
    uiState: TranslationDownloadUiState,
    isAnyDownloading: Boolean,
    onDeleteRequest: () -> Unit,
) {
    val platformLocale = LocalAppLocale.current.platformLocale
    val isComingSoon = row.isComingSoon
    val isDownloaded = row.isDownloaded
    val isSelected = row.id == uiState.selectedTranslation
    val isEnabled = !isComingSoon && isDownloaded

    val onSelect = {
        downloadVm.selectLanguage(row.id)
    }

    val onDownloadOrUpdate = {
        downloadVm.startDownload(row.id)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isComingSoon) 0.6f else 1f)
            .clickable(isEnabled) {
                if (isEnabled) onSelect()
            }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = {
                if (isEnabled) onSelect()
            },
            enabled = isEnabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorScheme.primary
            ),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = row.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) colorScheme.primary else colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            val subtitle = when {
                isComingSoon -> null
                !TranslationUtils.isBuiltInTranslation(row.id) -> when {
                    downloadStatus is ResourceDownloadStatus.InProgress -> String.format(
                        platformLocale, $$"%1$s %2$d%%", stringResource(R.string.downloading), downloadStatus.progress
                    )

                    downloadStatus is ResourceDownloadStatus.Started -> stringResource(R.string.downloading)
                    row.hasUpdate -> stringResource(R.string.updateAvailable)
                    isDownloaded -> stringResource(R.string.downloaded)
                    else -> null
                }

                else -> null
            }

            if (subtitle != null) {
                Text(
                    text = subtitle, style = MaterialTheme.typography.bodySmall, color = when {
                        downloadStatus is ResourceDownloadStatus.Failed -> colorScheme.error
                        row.hasUpdate -> colorScheme.primary
                        else -> colorScheme.onSurfaceVariant
                    }, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (isComingSoon) {
            ComingSoonBadge()
        } else if (!TranslationUtils.isBuiltInTranslation(row.id)) when (downloadStatus) {
            is ResourceDownloadStatus.InProgress -> {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        progress = { downloadStatus.progress / 100f },
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 2.dp,
                    )

                    AppIconButton(
                        painterResource(R.drawable.ic_x),
                        small = true,
                        contentDescription = stringResource(R.string.cancel),
                    ) {
                        downloadVm.cancelDownload(row.id)
                    }
                }
            }

            is ResourceDownloadStatus.Started -> {
                Loader(size = 24.dp)
            }

            is ResourceDownloadStatus.Failed -> {
                AppIconButton(
                    painterResource(R.drawable.ic_download),
                    onClick = onDownloadOrUpdate,
                )
            }

            else -> {
                if (!isDownloaded) {
                    AppIconButton(
                        painter = painterResource(R.drawable.ic_download),
                        enabled = !isAnyDownloading,
                        onClick = onDownloadOrUpdate,
                    )
                } else {
                    if (row.hasUpdate) {
                        AppIconButton(
                            painter = painterResource(R.drawable.ic_download),
                            enabled = !isAnyDownloading,
                            contentDescription = stringResource(R.string.update),
                            onClick = onDownloadOrUpdate,
                        )
                    }

                    AppIconButton(
                        painter = painterResource(R.drawable.ic_delete),
                        enabled = !isAnyDownloading,
                        tint = colorScheme.error,
                        onClick = onDeleteRequest
                    )
                }
            }
        }
    }
}

@Composable
private fun ComingSoonBadge() {
    Surface(
        shape = shapes.small,
        color = colorScheme.secondaryContainer,
    ) {
        Text(
            text = stringResource(R.string.comingSoon),
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
