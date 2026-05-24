package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.components.reader.ActionsProvider
import com.alfaazplus.sunnah.ui.components.reader.LocalHadithActions
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.text.buildHadithAnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutCollectionSheet(cwt: CollectionWithTranslation) {
    var showSheet by remember { mutableStateOf(false) }
    val colors by rememberUpdatedState(colorScheme)
    val translationId = ReaderPreferences.observeHadithTranslation()

    ActionsProvider {
        val actions = LocalHadithActions.current

        val infoAr = remember {
            cwt
                .getIntro("ar")
                ?.let {
                    buildHadithAnnotatedString(
                        it,
                        linkColor = colors.primary,
                        actions = actions,
                    )
                }
        }
        val infoEn = remember {
            cwt
                .getIntro(translationId)
                ?.let {
                    buildHadithAnnotatedString(
                        it,
                        linkColor = colors.primary,
                        actions = actions,
                    )
                }
        }

        if (infoAr.isNullOrEmpty() && infoEn.isNullOrEmpty()) {
            return@ActionsProvider
        }

        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .clip(shapes.small)
                .background(color = colorScheme.surfaceVariant)
                .clickable { showSheet = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .alpha(0.8f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(16.dp)
            )
            Text(
                text = stringResource(R.string.about_collection),
                style = typography.labelMedium,
            )
        }

        BottomSheet(
            isOpen = showSheet,
            onDismiss = {
                showSheet = false
            },
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .padding(bottom = 100.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (!infoAr.isNullOrEmpty()) {
                    Text(
                        text = infoAr,
                    )
                }

                if (!infoEn.isNullOrEmpty()) {
                    Text(
                        text = infoEn,
                    )
                }
            }
        }
    }
}
