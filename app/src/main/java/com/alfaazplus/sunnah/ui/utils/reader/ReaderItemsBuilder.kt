package com.alfaazplus.sunnah.ui.utils.reader

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

object ReaderItemsBuilder {
    suspend fun build(
        repo: HadithRepository2,
        bookId: String,
        params: TextBuilderParams,
    ): ReaderPreparedData? = withContext(Dispatchers.IO) {
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

        hadiths.forEach { hwc ->
            val chapterId = hwc.hadith.chapterId

            val chapter = if (chapterId != null && emittedChapterIds.add(chapterId)) {
                chapters[chapterId]
            } else null

            val narratorsCount = repo.dao.countNarratorsForHadith(hwc.hadithId)
            val primaryReference = repo.dao.getPrimaryReferenceForHadith(hwc.hadithId)

            val gradeText = hwc.grades
                .firstOrNull { it.lang == "en" }
                ?.let(HadithHelper::getHadithGradeText) ?: hwc.grades
                .firstOrNull()
                ?.let(HadithHelper::getHadithGradeText)

            val item = ReaderLayoutItem.HadithUI(
                hwc = hwc,
                chapterUi = buildChapter(chapter, params, arabicTextStyle, translationTextStyle),
                parsedArabicText = parseArabic(hwc, params, arabicTextStyle),
                parsedTranslationText = parseTranslation(hwc, params, translationTextStyle),
                hasNarratorsChain = narratorsCount > 0,
                visibleNumbering = primaryReference?.value ?: "${cwt.getTitle()}: ${hwc.hadith.number}",
                gradeText = gradeText,
                showDivider = true,
                key = "hadith-${hwc.hadith.id}",
            )

            items.add(item)
        }

        return@withContext ReaderPreparedData(bookId, items = items)
    }

    private fun buildChapter(
        chapter: ChapterWithTranslation?,
        params: TextBuilderParams,
        arabicTextStyle: TextStyle,
        translationTextStyle: TextStyle,
    ): HadithChapterUi? {
        if (chapter == null) return null

        val colors = params.uiConfig.colors

        val textParts = buildList {
            if (params.hadithTextOption != HadithTextOption.ONLY_TRANSLATION) {
                add("ar" to arabicTextStyle)
            }

            if (params.hadithTextOption != HadithTextOption.ONLY_ARABIC) {
                add("en" to translationTextStyle)
            }
        }

        val titles = textParts
            .mapNotNull { (langCode, textStyle) ->
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
                            withStyle(textStyle.toParagraphStyle()) {
                                withStyle(textStyle.toSpanStyle()) {
                                    append(raw)
                                }
                            }
                        }
                    }
            }
            .toMap()

        val intros = textParts
            .mapNotNull { (langCode, textStyle) ->
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
                            withStyle(textStyle.toParagraphStyle()) {
                                withStyle(textStyle.toSpanStyle()) {
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

    private fun parseArabic(hwc: HadithWithContents, params: TextBuilderParams, textStyle: TextStyle): AnnotatedString? {
        if (params.hadithTextOption == HadithTextOption.ONLY_TRANSLATION) return null

        val content = hwc.contents.firstOrNull { it.lang == "ar" } ?: return null

        val blocks = content.blocks

        if (blocks.isEmpty()) return null

        val colors = params.uiConfig.colors

        return buildAnnotatedString {
            withStyle(
                textStyle.toParagraphStyle()
            ) {
                withStyle(
                    textStyle.toSpanStyle()
                ) {
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
                            withStyle(
                                SpanStyle(
                                    color = colors.onBackground.alpha(0.75f)
                                )
                            ) {
                                append(text)
                            }
                        } else if (block.type == HadithBlockType.MATN) {
                            if (index > 0) append(" ")

                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
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

    private fun parseTranslation(hwc: HadithWithContents, params: TextBuilderParams, textStyle: TextStyle): AnnotatedString? {
        if (params.hadithTextOption == HadithTextOption.ONLY_ARABIC) return null

        val content = hwc.contents.firstOrNull { it.lang == "en" } ?: return null

        val blocks = content.blocks

        if (blocks.isEmpty()) return null

        val colors = params.uiConfig.colors

        return buildAnnotatedString {
            withStyle(
                textStyle.toParagraphStyle()
            ) {
                withStyle(
                    textStyle.toSpanStyle()
                ) {
                    blocks.forEach { block ->
                        if (block.text.isNullOrEmpty()) {
                            return@forEach
                        }

                        if (!params.isSanadEnabled && block.type == HadithBlockType.SANAD) {
                            return@forEach
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

                        if (block.type == HadithBlockType.NARRATOR) {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = textStyle.fontSize * 0.85,
                                )
                            ) {
                                append(text)
                            }
                        } else {
                            append(text)
                        }

                        if (lineBreak || block.type == HadithBlockType.NARRATOR) {
                            appendLine()
                        }
                    }
                }
            }
        }
    }
}
