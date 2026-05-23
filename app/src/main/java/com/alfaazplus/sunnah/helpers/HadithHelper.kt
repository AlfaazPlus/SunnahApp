package com.alfaazplus.sunnah.helpers

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.core.text.parseAsHtml
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeType
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.repository.hadith.HadithRepository2
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import com.alfaazplus.sunnah.ui.models.HadithOfTheDayHolder
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import java.util.Date

data class HadithGradeText(
    val type: HadithGradeType,
    val colors: Pair<Color, Color>,
    val label: String,
    val descriptions: List<String>,
)

object HadithHelper {
    const val PREBUILT_HADITHS_VERSION = 6

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

    fun getHadithGradeText(grade: HadithGradeEntity): HadithGradeText? {
        val gradeType = grade.gradeType

        val texts = when (gradeType) {
            HadithGradeType.SAHIH -> Triple("Sahih", "Authentic hadith", "صحيح")
            HadithGradeType.SAHIH_MAQTU -> Triple("Sahih Maqtu'", "Authentic narration from a Tabi‘i", "صحيح مقطوع")
            HadithGradeType.SAHIH_MARFU -> Triple("Sahih Marfu'", "Authentic narration attributed to the Prophet ﷺ", "صحيح مرفوع")
            HadithGradeType.SAHIH_MAUQUF -> Triple("Sahih Mawquf", "Authentic narration from a Companion", "صحيح موقوف")
            HadithGradeType.SAHIH_MAUQUF_MARFU -> Triple(
                "Sahih Mawquf with Marfu' ruling", "Companion narration treated as effectively prophetic", "صحيح موقوف له حكم المرفوع"
            )

            HadithGradeType.SAHIH_MUTAWATIR -> Triple("Sahih Mutawatir", "Mass-transmitted authentic hadith", "صحيح متواتر")
            HadithGradeType.HASAN -> Triple("Hasan", "Good and reliable hadith", "حسن")
            HadithGradeType.HASAN_SAHIH -> Triple("Hasan Sahih", "Hadith graded between Hasan and Sahih", "حسن صحيح")
            HadithGradeType.HASAN_MAQTU -> Triple("Hasan Maqtu'", "Good narration from a Tabi‘i", "حسن مقطوع")
            HadithGradeType.HASAN_MAUQUF -> Triple("Hasan Mawquf", "Good narration from a Companion", "حسن موقوف")
            HadithGradeType.HASAN_LI_GHAIRIH -> Triple("Hasan li-Ghayrih", "Weak narration strengthened by supporting chains", "حسن لغيره")
            HadithGradeType.DAIF -> Triple("Da'if", "Weak hadith", "ضعيف")
            HadithGradeType.DAIF_JIDDAN -> Triple("Very Weak", "Extremely weak hadith", "ضعيف جدًا")
            HadithGradeType.DAIF_MAQTU -> Triple("Weak Maqtu'", "Weak narration from a Tabi‘i", "ضعيف مقطوع")
            HadithGradeType.DAIF_MAQTU_MUNKAR -> Triple("Weak Munkar Maqtu'", "Rejected weak narration from a Tabi‘i", "ضعيف منكر مقطوع")
            HadithGradeType.DAIF_MARFU -> Triple("Weak Marfu'", "Weak narration attributed to the Prophet ﷺ", "ضعيف مرفوع")
            HadithGradeType.DAIF_MAUQUF -> Triple("Weak Mawquf", "Weak narration from a Companion", "ضعيف موقوف")
            HadithGradeType.DAIF_MUNKAR -> Triple("Weak Munkar", "Weak narration contradicting reliable narrators", "ضعيف منكر")
            HadithGradeType.DAIF_MURSAL -> Triple("Weak Mursal", "Narration with a missing Companion in the chain", "ضعيف مرسل")
            HadithGradeType.MAQTU -> Triple("Maqtu'", "Narration from a Tabi‘i", "مقطوع")
            HadithGradeType.MAUQUF -> Triple("Mawquf", "Narration from a Companion", "موقوف")
            HadithGradeType.MUNKAR -> Triple("Munkar", "Rejected narration due to contradiction or weakness", "منكر")
            HadithGradeType.MAWDU -> Triple("Mawdu'", "Fabricated hadith", "موضوع")
            HadithGradeType.SHADH -> Triple("Shadh", "Irregular narration contradicting stronger reports", "شاذ")
            HadithGradeType.SHADH_ANHA -> Triple("Shadh Anha", "Anomalous narration variant", "شاذ")
            HadithGradeType.SHADH_MAQTU -> Triple("Shadh Maqtu'", "Irregular narration from a Tabi‘i", "شاذ مقطوع")
            HadithGradeType.OTHER -> return null
        }

        val colors = when {
            gradeType.type.startsWith("sahih") -> Color(76, 175, 80) to Color.White
            gradeType.type.startsWith("hasan") -> Color(157, 145, 43) to Color.Black
            gradeType.type.startsWith("daif") -> Color(244, 67, 54) to Color.White
            else -> Color(158, 11, 0) to Color.White
        }

        return HadithGradeText(
            type = gradeType,
            colors = colors,
            label = "${texts.first} (${texts.third})",
            descriptions = listOf(
                grade.label,
                texts.second,
            ).filter { it.isNotEmpty() },
        )
    }

    suspend fun getHadithOfTheDay(repo: HadithRepository2): HadithOfTheDay? {
        val hotdKey = stringPreferencesKey(Keys.HADITH_OF_THE_DAY)

        val hotdValue = DataStoreManager.readFirst(hotdKey, "")
        val hotdHolder = HadithOfTheDayHolder.parse(hotdValue)

        if (hotdHolder != null) {
            val cached = repo.getHotd(hotdHolder.hadithId)

            if (cached != null) {
                return cached
            }

            DataStoreManager.write(hotdKey, "")
        }

        val maxPreviewLength = 300
        var attempts = 0

        while (attempts <= 100) {
            val hwc = repo.getRandomSahihHadith() ?: break

            val preview = hwc.hotdPreviewText()

            if (preview != null && preview
                    .parseAsHtml()
                    .toString().length <= maxPreviewLength
            ) {
                return persistHotd(hotdKey, repo, hwc)
            }

            attempts++
        }

        return null
    }

    private fun HadithWithContents.hotdPreviewText(): String? {
        val blocks = contents
            .firstOrNull { it.lang != "ar" }
            ?.blocks ?: return null

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
        repo: HadithRepository2,
        hwc: HadithWithContents,
    ): HadithOfTheDay {
        val holder = HadithOfTheDayHolder(hwc.hadithId, Date(System.currentTimeMillis()))

        DataStoreManager.write(key, holder.toString())

        return HadithOfTheDay(
            hwc = hwc,
            collectionName = repo.getCollectionName(hwc.hadith.collectionId),
        )
    }

    fun shareHadith(
        context: Context,
        hwc: HadithWithContents,
        collectionName: String?,
        bookName: String?,
        translationLangCode: String = "en",
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
