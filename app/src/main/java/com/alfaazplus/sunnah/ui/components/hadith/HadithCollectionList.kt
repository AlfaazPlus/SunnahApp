package com.alfaazplus.sunnah.ui.components.hadith

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.entities.v2.CollectionType
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.metadataLangCodes
import com.alfaazplus.sunnah.ui.utils.text.textStyle
import com.alfaazplus.sunnah.ui.viewModels.CollectionListViewModel

@Composable
fun HadithCollectionList(
    onCollectionClick: (collectionId: String) -> Unit,
    vm: CollectionListViewModel = hiltViewModel(),
) {
    val collections by vm.collections.collectAsState()
    val translationLangCode = ReaderPreferences.observeHadithTranslation()

    val grouped by remember {
        derivedStateOf {
            collections.groupBy { it.collection.type }
        }
    }

    val decorationTint = colorScheme.primary

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        grouped.forEach { (type, items) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(R.drawable.vector_1),
                    contentDescription = null,
                    modifier = Modifier.height(24.dp),
                    colorFilter = ColorFilter.tint(decorationTint)
                )

                Text(
                    when (type) {
                        CollectionType.COLLECTION -> stringResource(R.string.primaryCollections)
                        CollectionType.SELECTION -> stringResource(R.string.selection)
                    },
                    style = typography.titleSmall,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    textAlign = TextAlign.Center
                )

                Image(
                    painterResource(R.drawable.vector_1),
                    contentDescription = null,
                    modifier = Modifier
                        .height(24.dp)
                        .scale(-1f, 1f),
                    colorFilter = ColorFilter.tint(decorationTint)
                )
            }

            items
                .chunked(2)
                .forEach {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        it.forEach { cwt ->
                            HadithCollectionItem(
                                cwt = cwt,
                                translationLangCode = translationLangCode,
                            ) {
                                onCollectionClick(cwt.collection.id)
                            }
                        }
                    }
                }
        }

    }
}

@Composable
private fun RowScope.HadithCollectionItem(
    cwt: CollectionWithTranslation,
    translationLangCode: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.weight(1f),
        color = colorScheme.surfaceContainerLow,
        shape = shapes.medium,
        border = BorderStroke(1.dp, colorScheme.primary.alpha(0.15f)),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            metadataLangCodes(translationLangCode).forEach { langCode ->
                cwt
                    .getTitle(langCode)
                    ?.let {
                        Text(
                            text = it,
                            style = textStyle(
                                langCode = langCode,
                                fontSize = typography.titleSmall.fontSize,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }
            }
        }
    }
}
