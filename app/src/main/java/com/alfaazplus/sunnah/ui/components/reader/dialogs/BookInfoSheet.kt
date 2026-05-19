package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.components.reader.ActionsProvider
import com.alfaazplus.sunnah.ui.components.reader.LocalHadithActions
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.text.buildStyledHadithAnnotatedString
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString

private data class BookContentBlock(
    val langCode: String,
    val text: AnnotatedString,
)

private data class BookInfoSection(
    @StringRes
    val titleRes: Int,
    val blocks: List<BookContentBlock>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookInfoSheet(bwt: BookWithTranslation?, onClose: () -> Unit) {
    ActionsProvider {
        BottomSheet(
            isOpen = bwt != null,
            onDismiss = onClose,
            title = stringResource(R.string.book_info),
        ) {
            if (bwt != null) {
                Content(bwt)
            }
        }
    }
}

@Composable
private fun Content(bwt: BookWithTranslation) {
    val hadithTextOption = ReaderPreferences.observeHadithTextOption()
    val translationLangCode = ReaderPreferences.observeHadithTranslation()
    val arabicSizePercent = ReaderPreferences.observeTextSizePercentArabic()
    val translationSizePercent = ReaderPreferences.observeTextSizePercentTranslation()
    val isSerifFontStyle = ReaderPreferences.observeIsSerifFontStyle()

    val preambleBlocks = rememberBookContentBlocks(
        bwt,
        translationLangCode,
        hadithTextOption,
        arabicSizePercent,
        translationSizePercent,
        isSerifFontStyle,
        bwt::getPreamble,
    )
    val notesBlocks = rememberBookContentBlocks(
        bwt,
        translationLangCode,
        hadithTextOption,
        arabicSizePercent,
        translationSizePercent,
        isSerifFontStyle,
        bwt::getNotes,
    )
    val introBlocks = rememberBookContentBlocks(
        bwt,
        translationLangCode,
        hadithTextOption,
        arabicSizePercent,
        translationSizePercent,
        isSerifFontStyle,
        bwt::getIntro,
    )

    val sections = remember(preambleBlocks, notesBlocks, introBlocks) {
        listOfNotNull(
            preambleBlocks
                .takeIf { it.isNotEmpty() }
                ?.let {
                    BookInfoSection(R.string.book_section_preamble, it)
                },
            notesBlocks
                .takeIf { it.isNotEmpty() }
                ?.let {
                    BookInfoSection(R.string.book_section_notes, it)
                },
            introBlocks
                .takeIf { it.isNotEmpty() }
                ?.let {
                    BookInfoSection(R.string.book_section_intro, it)
                },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BookInfoHeader(bwt, translationLangCode)

        if (sections.isEmpty()) {
            Text(
                text = stringResource(R.string.book_no_details),
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
        } else {
            SelectionContainer {
                sections.forEach { section ->
                    BookInfoSectionCard(section)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookInfoHeader(bwt: BookWithTranslation, translationLangCode: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes.large,
        color = colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            bwt.book.number
                ?.takeIf { it.isNotEmpty() }
                ?.let { number ->
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.vector_bg2),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorScheme.primary),
                            modifier = Modifier.fillMaxSize(),
                        )

                        Text(
                            text = number,
                            style = typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onPrimaryContainer,
                        )
                    }
                }

            bwt
                .getTitle("ar")
                ?.let { arTitle ->
                    Text(
                        text = arTitle,
                        style = typography.headlineSmall.copy(fontFamily = fontUthmani),
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }

            bwt
                .getTitle(translationLangCode)
                ?.let { enTitle ->
                    Text(
                        text = enTitle
                            .parseAsHtml()
                            .toAnnotatedString(),
                        style = typography.titleMedium,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                bwt.hadithCount
                    ?.takeIf { it > 0 }
                    ?.let { count ->
                        MetadataChip(
                            label = stringResource(R.string.hadiths),
                            value = count.toString(),
                        )
                    }
            }
        }
    }
}

@Composable
private fun MetadataChip(
    label: String,
    value: String,
) {
    Surface(
        shape = shapes.small,
        color = colorScheme.secondary,
    ) {
        Text(
            text = "$label: $value",
            style = typography.labelMedium,
            color = colorScheme.onSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun BookInfoSectionCard(section: BookInfoSection) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(section.titleRes),
            style = typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary,
        )

        HorizontalDivider(color = colorScheme.outline.alpha(0.25f))

        section.blocks.forEachIndexed { index, block ->
            if (index > 0) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = colorScheme.outline.alpha(0.15f))
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = block.text,
                color = colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun rememberBookContentBlocks(
    bwt: BookWithTranslation,
    translationLangCode: String,
    textOption: HadithTextOption,
    arabicSizePercent: Int,
    translationSizePercent: Int,
    isSerifFontStyle: Boolean,
    getter: (String) -> String?,
): List<BookContentBlock> {
    val colors by rememberUpdatedState(colorScheme)
    val type by rememberUpdatedState(typography)
    val actions = LocalHadithActions.current

    return remember(
        bwt,
        translationLangCode,
        textOption,
        colors,
        type,
        actions,
        arabicSizePercent,
        translationSizePercent,
        isSerifFontStyle,
    ) {
        val langCodes = buildList {
            if (textOption != HadithTextOption.ONLY_ARABIC) {
                add(translationLangCode)
            }

            if (textOption != HadithTextOption.ONLY_TRANSLATION) {
                add("ar")
            }
        }

        langCodes.mapNotNull { lang ->
            getter(lang)?.let { raw ->
                BookContentBlock(
                    langCode = lang,
                    text = buildStyledHadithAnnotatedString(
                        text = raw,
                        langCode = lang,
                        colors = colors,
                        type = type,
                        arabicSizePercent = arabicSizePercent,
                        translationSizePercent = translationSizePercent,
                        isSerifFontStyle = isSerifFontStyle,
                        actions = actions,
                    ),
                )
            }
        }
    }
}
