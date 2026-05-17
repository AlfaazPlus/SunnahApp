package com.alfaazplus.sunnah.ui.utils.reader

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.repository.hadith.HadithRepository2
import com.alfaazplus.sunnah.ui.models.HadithChapterUi
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.text.ArabicTextStyleParams
import com.alfaazplus.sunnah.ui.utils.text.TextBuilderParams
import com.alfaazplus.sunnah.ui.utils.text.TranslationTextStyleParams
import com.alfaazplus.sunnah.ui.utils.text.buildHadithAnnotatedString
import com.alfaazplus.sunnah.ui.utils.text.getArabicTextStyle
import com.alfaazplus.sunnah.ui.utils.text.getTranslationTextStyle
import com.alfaazplus.sunnah.ui.utils.text.parseHadithText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

data class ReaderPreparedData(
    val bookId: String,
    val items: List<ReaderLayoutItem>,
)

private data class BuilderSessionStyle(
    val arParagraphStyle: ParagraphStyle,
    val arSpanStyle: SpanStyle,
    val trParagraphStyle: ParagraphStyle,
    val trTightParagraphStyle: ParagraphStyle,
    val trSpanStyle: SpanStyle,
)

object ReaderItemsBuilder {
    private var sessionStyle: BuilderSessionStyle? = null

    fun initializeStyles(params: TextBuilderParams) {
        val arabicTextStyle = getArabicTextStyle(
            ArabicTextStyleParams(
                colors = params.uiConfig.colors,
                type = params.uiConfig.type,
                sizePercent = params.arabicSizePercent,
            )
        )

        val translationTextStyle = getTranslationTextStyle(
            TranslationTextStyleParams(
                colors = params.uiConfig.colors,
                type = params.uiConfig.type,
                sizePercent = params.arabicSizePercent,
                isSerif = params.isSerifFontStyle
            )
        )

        sessionStyle = BuilderSessionStyle(
            arParagraphStyle = arabicTextStyle.toParagraphStyle(),
            arSpanStyle = arabicTextStyle.toSpanStyle(),
            trParagraphStyle = translationTextStyle.toParagraphStyle(),
            trTightParagraphStyle = translationTextStyle
                .copy(
                    lineHeight = translationTextStyle.fontSize,
                    lineHeightStyle = LineHeightStyle.Default.copy(
                        mode = LineHeightStyle.Mode.Tight,
                        alignment = LineHeightStyle.Alignment.Center,
                    ),
                )
                .toParagraphStyle(),
            trSpanStyle = translationTextStyle.toSpanStyle(),
        )
    }

    suspend fun build(
        repo: HadithRepository2,
        bookId: String,
        params: TextBuilderParams,
    ): ReaderPreparedData? = withContext(Dispatchers.IO) {
        initializeStyles(params)

        val hadithsDeferred = async {
            repo.dao.getHadithsForBook(bookId)
        }

        val chaptersDeferred = async {
            repo.dao
                .getChaptersForBook(bookId)
                .associateBy { it.chapter.id }
        }

        val hadiths = hadithsDeferred.await()
        val chapters = chaptersDeferred.await()
        val cwt = repo.dao.getCollectionByBookId(bookId) ?: return@withContext null

        if (hadiths.isEmpty()) return@withContext null

        val items = ArrayList<ReaderLayoutItem>()
        val emittedChapterIds = HashSet<String>()

        hadiths.forEach { hwc ->
            val chapterId = hwc.hadith.chapterId

            val chapter = if (chapterId != null && emittedChapterIds.add(chapterId)) {
                chapters[chapterId]
            } else null

            items.add(
                buildHadithUi(
                    repo = repo,
                    hwc = hwc,
                    params = params,
                    chapterUi = buildChapter(chapter, params),
                    numberingFallback = "${cwt.getTitle()}: ${hwc.hadith.number}",
                    showDivider = true,
                )
            )
        }

        return@withContext ReaderPreparedData(bookId, items = items)
    }

    suspend fun buildQuickReferenceItems(
        repo: HadithRepository2,
        hadithIds: List<String>,
        params: TextBuilderParams,
    ): List<ReaderLayoutItem.HadithUI> = withContext(Dispatchers.IO) {
        initializeStyles(params)

        hadithIds
            .distinct()
            .mapNotNull { id ->
                val hwc = repo.dao.getHadithById(id) ?: return@mapNotNull null

                buildHadithUi(
                    repo = repo,
                    hwc = hwc,
                    params = params,
                    chapterUi = null,
                    numberingFallback = hwc.hadith.number ?: id,
                    showDivider = true,
                )
            }
    }

    private suspend fun buildHadithUi(
        repo: HadithRepository2,
        hwc: HadithWithContents,
        params: TextBuilderParams,
        chapterUi: HadithChapterUi?,
        numberingFallback: String,
        showDivider: Boolean,
    ): ReaderLayoutItem.HadithUI {
        val narratorsCount = repo.dao.countNarratorsForHadith(hwc.hadithId)
        val primaryReference = repo.dao.getPrimaryReferenceForHadith(hwc.hadithId)

        val gradeText = hwc.grades
            .firstOrNull { it.lang == "en" }
            ?.let(HadithHelper::getHadithGradeText) ?: hwc.grades
            .firstOrNull()
            ?.let(HadithHelper::getHadithGradeText)

        return ReaderLayoutItem.HadithUI(
            hwc = hwc,
            chapterUi = chapterUi,
            parsedArabicText = parseArabic(hwc, params),
            parsedTranslationText = parseTranslation(hwc, params),
            hasNarratorsChain = narratorsCount > 0,
            visibleNumbering = primaryReference?.value ?: numberingFallback,
            gradeText = gradeText,
            showDivider = showDivider,
            key = "hadith-${hwc.hadith.id}",
        )
    }

    private fun buildChapter(chapter: ChapterWithTranslation?, params: TextBuilderParams): HadithChapterUi? {
        if (chapter == null) return null

        val colors = params.uiConfig.colors
        val styles = sessionStyle!!

        val textParts = buildList {
            if (params.hadithTextOption != HadithTextOption.ONLY_ARABIC) {
                add(Triple("en", styles.trParagraphStyle, styles.trSpanStyle))
            }

            if (params.hadithTextOption != HadithTextOption.ONLY_TRANSLATION) {
                add(Triple("ar", styles.arParagraphStyle, styles.arSpanStyle))
            }
        }

        val titles = textParts
            .mapNotNull { (langCode, paragraphStyle, spanStyle) ->
                chapter
                    .getTitle(langCode)
                    ?.let {
                        buildHadithAnnotatedString(
                            it,
                            linkColor = colors.primary,
                            actions = params.hadithActions,
                        )
                    }
                    ?.let { raw ->
                        langCode to buildAnnotatedString {
                            paragraph(paragraphStyle) {
                                span(spanStyle) {
                                    append(raw)
                                }
                            }
                        }
                    }
            }
            .toMap()

        val intros = textParts
            .mapNotNull { (langCode, paragraphStyle, spanStyle) ->
                chapter
                    .getIntro(langCode)
                    ?.let {
                        buildHadithAnnotatedString(
                            it,
                            linkColor = colors.primary,
                            actions = params.hadithActions,
                        )
                    }
                    ?.let { raw ->
                        langCode to buildAnnotatedString {
                            paragraph(paragraphStyle) {
                                span(spanStyle) {
                                    append(raw)
                                }
                            }
                        }
                    }
            }
            .toMap()

        if (titles.isEmpty() && intros.isEmpty()) {
            return null
        }

        return HadithChapterUi(
            chapter = chapter,
            titles = titles,
            intros = intros,
        )
    }

    private fun parseArabic(hwc: HadithWithContents, params: TextBuilderParams): AnnotatedString? {
        if (params.hadithTextOption == HadithTextOption.ONLY_TRANSLATION) return null

        val content = hwc.contents.firstOrNull { it.lang == "ar" } ?: return null

        val blocks = content.blocks

        if (blocks.isEmpty()) return null

        val colors = params.uiConfig.colors
        val styles = sessionStyle!!

        return buildAnnotatedString {
            paragraph(styles.arParagraphStyle) {
                span(styles.arSpanStyle) {
                    blocks.forEachIndexed { index, block ->
                        if (block.text.isNullOrEmpty()) {
                            return@forEachIndexed
                        }

                        if (!params.isSanadEnabled && block.type == HadithBlockType.SANAD) {
                            return@forEachIndexed
                        }

                        val lineBreak =
                            block.type == HadithBlockType.COMMENTARY || block.type == HadithBlockType.NOTE || block.type == HadithBlockType.UNKNOWN


                        if (lineBreak) {
                            appendLine()
                        }

                        val text = buildHadithAnnotatedString(
                            parts = parseHadithText(block.text),
                            linkColor = colors.primary,
                            actions = params.hadithActions,
                        )

                        if (block.type == HadithBlockType.SANAD) {
                            span(SpanStyle(color = colors.onBackground.alpha(0.75f))) {
                                append(text)
                            }
                        } else if (block.type == HadithBlockType.MATN) {
                            if (index > 0) append(" ")

                            span(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(text)
                            }

                            if (index < blocks.lastIndex) append(" ")
                        } else {
                            append(text)
                        }

                        if (lineBreak) {
                            appendLine()
                        }
                    }
                }
            }
        }
    }

    private fun parseTranslation(hwc: HadithWithContents, params: TextBuilderParams): AnnotatedString? {
        if (params.hadithTextOption == HadithTextOption.ONLY_ARABIC) return null

        val content = hwc.contents.firstOrNull { it.lang == "en" } ?: return null

        val blocks = content.blocks

        if (blocks.isEmpty()) return null

        val colors = params.uiConfig.colors

        val styles = sessionStyle!!

        return buildAnnotatedString {
            blocks.forEachIndexed { index, block ->
                if (block.text.isNullOrEmpty()) {
                    return@forEachIndexed
                }

                if (!params.isSanadEnabled && block.type == HadithBlockType.SANAD) {
                    return@forEachIndexed
                }

                paragraph(if (block.type == HadithBlockType.NARRATOR) styles.trTightParagraphStyle else styles.trParagraphStyle) {
                    val text = buildHadithAnnotatedString(
                        parts = parseHadithText(block.text),
                        linkColor = colors.primary,
                        actions = params.hadithActions,
                    )

                    if (block.type == HadithBlockType.NARRATOR) {
                        span(
                            styles.trSpanStyle.merge(
                                SpanStyle(
                                    fontSize = styles.trSpanStyle.fontSize * 0.85,
                                    color = colors.onBackground.alpha(0.7f),
                                )
                            )
                        ) {
                            append(text)
                        }
                    } else {
                        span(styles.trSpanStyle) {
                            append(text)
                        }
                    }

                    if (index != blocks.lastIndex) {
                        append("\n")
                    }
                }
            }
        }
    }
}

fun AnnotatedString.Builder.paragraph(style: ParagraphStyle, enabled: Boolean = true, block: () -> Unit) {
    if (enabled) {
        withStyle(style) {
            block()
        }
    } else {
        block()
    }
}

fun AnnotatedString.Builder.span(style: SpanStyle, enabled: Boolean = true, block: () -> Unit) {
    if (enabled) {
        withStyle(style) {
            block()
        }
    } else {
        block()
    }
}

