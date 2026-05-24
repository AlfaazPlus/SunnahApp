package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.ui.theme.tightTextStyle
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.metadataLangCodes
import com.alfaazplus.sunnah.ui.utils.text.textStyleForLang
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString


@Composable
fun BookItemCard(
    modifier: Modifier = Modifier,
    bwt: BookWithTranslation,
    isCurrent: Boolean = false,
    onClick: () -> Unit,
) {
    val translationLangCode = ReaderPreferences.observeHadithTranslation()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isCurrent) colorScheme.primary
            else colorScheme.outlineVariant.copy(alpha = 0.4f),
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (!bwt.book.number.isNullOrEmpty()) {
                    Image(
                        painterResource(R.drawable.vector_bg2),
                        null,
                        colorFilter = ColorFilter.tint(colorScheme.primary),
                    )
                    Text(
                        text = bwt.book.number,
                        style = typography.labelMedium,
                        fontWeight = FontWeight.Normal,
                        color = colorScheme.onPrimary,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                metadataLangCodes(translationLangCode).forEach { langCode ->
                    bwt
                        .getTitle(langCode)
                        ?.let {
                            Text(
                                text = it
                                    .parseAsHtml()
                                    .toAnnotatedString(),
                                style = textStyleForLang(langCode)
                                    .merge(tightTextStyle)
                                    .copy(
                                        fontSize = typography.titleSmall.fontSize,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                textAlign = TextAlign.Center,
                            )
                        }
                }
            }

            Spacer(Modifier.width(36.dp))
        }
    }
}
