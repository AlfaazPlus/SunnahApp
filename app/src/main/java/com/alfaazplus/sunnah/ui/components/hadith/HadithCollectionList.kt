package com.alfaazplus.sunnah.ui.components.hadith

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.db.entities.v2.CollectionType
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslations
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.theme.tightTextStyle
import com.alfaazplus.sunnah.ui.viewModels.CollectionListViewModel
import com.alfaazplus.sunnah.ui.viewModels.DownloadCollectionViewModel


@Composable
private fun HadithCollectionItem(
    isDownloading: Boolean,
    cwt: CollectionWithTranslations,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorScheme.surfaceContainer,
        shape = shapes.medium,
        border = BorderStroke(1.dp, colorScheme.outline.alpha(0.15f)),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        ) {
            cwt
                .getTitle("en")
                ?.let {
                    Text(
                        text = it,
                        style = typography.titleSmall,
                        textAlign = TextAlign.Center,
                    )
                }

            cwt
                .getTitle("ar")
                ?.let {
                    Text(
                        text = it,
                        style = typography.titleMedium
                            .merge(tightTextStyle)
                            .copy(
                                fontFamily = fontUthmani,
                            ),
                        textAlign = TextAlign.Center,
                    )
                }
        }
    }
}

@Composable
fun HadithCollectionList(
    onCollectionClick: (collectionId: String) -> Unit,
    vm: CollectionListViewModel = hiltViewModel(),
    downloadVm: DownloadCollectionViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        downloadVm.refreshDownloadStates()
    }

    val collections by vm.collections.collectAsState()
    val downloadStates by downloadVm
        .getAllDownloadStates()
        .collectAsState(initial = emptyMap())

    val grouped by remember {
        derivedStateOf {
            collections.groupBy { it.collection.type }
        }
    }

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        grouped.forEach { (type, items) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HorizontalDivider(
                    Modifier.weight(1f),
                )

                Text(
                    when (type) {
                        CollectionType.COLLECTION -> "Primary Collections (لمصدر الأصلي)"
                        CollectionType.SELECTION -> "Selection (المصادر الثانوية)"
                    },
                    style = typography.titleSmall,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                HorizontalDivider(
                    Modifier.weight(1f),
                )
            }

            items
                .chunked(1)
                .forEach {
                    Row {
                        it.forEach { cwt ->
                            val workInfo = downloadStates[cwt.collection.id]
                            val isDownloading = workInfo?.state?.isFinished != null && !workInfo.state.isFinished

                            HadithCollectionItem(isDownloading, cwt) {
                                if (isDownloading || !cwt.isDownloaded) {
                                    // navController.navigate(Routes.SETTINGS_MANAGE_COLLECTIONS)
                                } else {
                                    onCollectionClick(cwt.collection.id)
                                }
                            }
                        }
                    }
                }
        }

    }
}
