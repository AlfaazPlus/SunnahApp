package com.alfaazplus.sunnah.helpers

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.core.text.parseAsHtml
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeType
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import com.alfaazplus.sunnah.ui.models.HadithOfTheDayHolder
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import java.util.Date

data class HadithGradeText(
    val type: HadithGradeType,
    val colors: Pair<Color, Color>,
    val label: String,
    val descriptions: List<String>,
)

object HadithHelper {
    const val PREBUILT_HADITHS_VERSION = 1

    val INCLUDED_COLLECTIONS: Set<String> = setOf(
        "bukhari",
        "muslim",
        "nasai",
        "abudawud",
        "tirmidhi",
        "ibnmajah",
        "malik",
        "riyadussalihin",
        "forty",
    )

    fun isIncludedCollection(id: String): Boolean {
        return INCLUDED_COLLECTIONS.contains(id)
    }

    fun getHadithGradeText(ctx: Context, grade: HadithGradeEntity): HadithGradeText? {
        val gradeType = grade.gradeType

        val (labelRes, descRes) = when (gradeType) {
            HadithGradeType.SAHIH -> R.string.hadith_grade_sahih to R.string.hadith_grade_sahih_desc
            HadithGradeType.SAHIH_MAQTU -> R.string.hadith_grade_sahih_maqtu to R.string.hadith_grade_sahih_maqtu_desc
            HadithGradeType.SAHIH_MARFU -> R.string.hadith_grade_sahih_marfu to R.string.hadith_grade_sahih_marfu_desc
            HadithGradeType.SAHIH_MAUQUF -> R.string.hadith_grade_sahih_mauquf to R.string.hadith_grade_sahih_mauquf_desc
            HadithGradeType.SAHIH_MAUQUF_MARFU -> R.string.hadith_grade_sahih_mauquf_marfu to R.string.hadith_grade_sahih_mauquf_marfu_desc
            HadithGradeType.SAHIH_MUTAWATIR -> R.string.hadith_grade_sahih_mutawatir to R.string.hadith_grade_sahih_mutawatir_desc
            HadithGradeType.HASAN -> R.string.hadith_grade_hasan to R.string.hadith_grade_hasan_desc
            HadithGradeType.HASAN_SAHIH -> R.string.hadith_grade_hasan_sahih to R.string.hadith_grade_hasan_sahih_desc
            HadithGradeType.HASAN_MAQTU -> R.string.hadith_grade_hasan_maqtu to R.string.hadith_grade_hasan_maqtu_desc
            HadithGradeType.HASAN_MAUQUF -> R.string.hadith_grade_hasan_mauquf to R.string.hadith_grade_hasan_mauquf_desc
            HadithGradeType.HASAN_LI_GHAIRIH -> R.string.hadith_grade_hasan_li_ghairih to R.string.hadith_grade_hasan_li_ghairih_desc
            HadithGradeType.DAIF -> R.string.hadith_grade_daif to R.string.hadith_grade_daif_desc
            HadithGradeType.DAIF_JIDDAN -> R.string.hadith_grade_daif_jiddan to R.string.hadith_grade_daif_jiddan_desc
            HadithGradeType.DAIF_MAQTU -> R.string.hadith_grade_daif_maqtu to R.string.hadith_grade_daif_maqtu_desc
            HadithGradeType.DAIF_MAQTU_MUNKAR -> R.string.hadith_grade_daif_maqtu_munkar to R.string.hadith_grade_daif_maqtu_munkar_desc
            HadithGradeType.DAIF_MARFU -> R.string.hadith_grade_daif_marfu to R.string.hadith_grade_daif_marfu_desc
            HadithGradeType.DAIF_MAUQUF -> R.string.hadith_grade_daif_mauquf to R.string.hadith_grade_daif_mauquf_desc
            HadithGradeType.DAIF_MUNKAR -> R.string.hadith_grade_daif_munkar to R.string.hadith_grade_daif_munkar_desc
            HadithGradeType.DAIF_MURSAL -> R.string.hadith_grade_daif_mursal to R.string.hadith_grade_daif_mursal_desc
            HadithGradeType.MAQTU -> R.string.hadith_grade_maqtu to R.string.hadith_grade_maqtu_desc
            HadithGradeType.MAUQUF -> R.string.hadith_grade_mauquf to R.string.hadith_grade_mauquf_desc
            HadithGradeType.MUNKAR -> R.string.hadith_grade_munkar to R.string.hadith_grade_munkar_desc
            HadithGradeType.MAWDU -> R.string.hadith_grade_mawdu to R.string.hadith_grade_mawdu_desc
            HadithGradeType.SHADH -> R.string.hadith_grade_shadh to R.string.hadith_grade_shadh_desc
            HadithGradeType.SHADH_ANHA -> R.string.hadith_grade_shadh_anha to R.string.hadith_grade_shadh_anha_desc
            HadithGradeType.SHADH_MAQTU -> R.string.hadith_grade_shadh_maqtu to R.string.hadith_grade_shadh_maqtu_desc
            HadithGradeType.OTHER -> return null
        }

        val texts = Pair(
            ctx.getString(labelRes),
            ctx.getString(descRes),
        )

        val colors = when {
            gradeType.type.startsWith("sahih") -> Color(76, 175, 80) to Color.White
            gradeType.type.startsWith("hasan") -> Color(157, 145, 43) to Color.Black
            gradeType.type.startsWith("daif") -> Color(244, 67, 54) to Color.White
            else -> Color(158, 11, 0) to Color.White
        }

        return HadithGradeText(
            type = gradeType,
            colors = colors,
            label = ctx.getString(R.string.hadith_grade_label_format, texts.first),
            descriptions = listOf(
                grade.label,
                texts.second,
            ).filter { it.isNotEmpty() },
        )
    }

    suspend fun getHadithOfTheDay(repo: HadithRepository): HadithOfTheDay? {
        val hotdKey = stringPreferencesKey(Keys.HADITH_OF_THE_DAY)

        val hotdValue = DataStoreManager.readFirst(hotdKey, "")
        val hotdHolder = HadithOfTheDayHolder.parse(hotdValue)

        if (hotdHolder != null) {
            val langCode = ReaderPreferences.getHadithTranslation()
            val cached = repo.getHotd(hotdHolder.hadithId, langCode)

            if (cached != null) {
                return cached
            }

            DataStoreManager.write(hotdKey, "")
        }

        val minPreviewLength = 50
        val maxPreviewLength = 300
        var attempts = 0

        while (attempts <= 100) {
            val hwc = repo.getRandomSahihHadith() ?: break

            val preview = hwc.hotdPreviewText()
            val length = preview
                ?.parseAsHtml()
                ?.toString()?.length ?: 0

            if (length in minPreviewLength..maxPreviewLength) {
                return persistHotd(hotdKey, repo, hwc)
            }

            attempts++
        }

        return null
    }

    private fun HadithWithContents.hotdPreviewText(): String? {
        val blocks = contents.firstOrNull { it.lang != "ar" }?.blocks ?: return null

        return buildString {
            blocks.forEach { block ->
                if (block.type == HadithBlockType.NARRATOR || block.type == HadithBlockType.MATN) {
                    block.text?.let { append(it) }
                }
            }
        }.takeIf { it.isNotBlank() }
    }

    private suspend fun persistHotd(
        key: Preferences.Key<String>,
        repo: HadithRepository,
        hwc: HadithWithContents,
    ): HadithOfTheDay {
        val holder = HadithOfTheDayHolder(hwc.hadithId, Date(System.currentTimeMillis()))

        DataStoreManager.write(key, holder.toString())

        val langCode = ReaderPreferences.getHadithTranslation()

        return HadithOfTheDay(
            hwc = hwc,
            collectionName = repo.getCollectionName(hwc.hadith.collectionId, langCode),
        )
    }

    fun shareHadith(
        context: Context,
        hwc: HadithWithContents,
        collectionName: String?,
        bookName: String?,
        translationLangCode: String,
    ) {
        val content = hwc.contents.firstOrNull { it.lang == translationLangCode } ?: return

        val textToShare = buildString {
            content.blocks.forEach {
                appendLine(it.text)
                appendLine()
            }

            appendLine("Reference:")
            appendLine("Hadith Number: ${hwc.hadith.number ?: ""}")

            if (!collectionName.isNullOrEmpty()) {
                appendLine(collectionName)
            }

            if (!bookName.isNullOrEmpty()) {
                appendLine("Book: $bookName")
            }
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textToShare)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share via")
        )
    }
}
