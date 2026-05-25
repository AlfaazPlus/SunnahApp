package com.alfaazplus.sunnah.ui.models

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.text.AnnotatedString
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.alfaazplus.sunnah.db.entities.v2.BookEntity

enum class SearchResultTab(val type: Int) {
    Hadiths(0),
    Books(1),
    Scholars(2);

    companion object {
        fun fromType(type: Int): SearchResultTab {
            return entries.firstOrNull { it.type == type } ?: Hadiths
        }
    }
}

val SearchResultTabSaver: Saver<SearchResultTab, Any> = listSaver(
    save = { listOf(it.type) },
    restore = { SearchResultTab.fromType(it[0]) },
)

/** Room paging projection; mapped to [HadithSearchResult] in the repository. */
data class HadithSearchRow(
    @ColumnInfo(name = "hadith_id") val hadithId: String,
    @ColumnInfo(name = "book_id") val bookId: String,
    @ColumnInfo(name = "collection_id") val collectionId: String,
    @ColumnInfo(name = "hadith_number") val hadithNumber: String?,
    @ColumnInfo(name = "collection_name") val collectionName: String,
    @ColumnInfo(name = "blocks_json") val blocksJson: String,
    @ColumnInfo(name = "matched_lang") val matchedLang: String,
)

data class HadithSearchResult(
    val hadithId: String,
    val bookId: String,
    val collectionId: String,
    val numbering: AnnotatedString,
    val matchedLang: String,
    val snippetText: AnnotatedString,
) {
    override fun equals(other: Any?) =
        this === other || (other is HadithSearchResult && hadithId == other.hadithId)

    override fun hashCode(): Int = hadithId.hashCode()
}

data class BooksSearchResult(
    @Embedded val book: BookEntity,
    @ColumnInfo(name = "collection_name") val collectionName: String,
    @ColumnInfo(name = "title_en") val titleEn: String?,
    @ColumnInfo(name = "title_ar") val titleAr: String?,
    @ColumnInfo(name = "hadith_count") val hadithCount: Int,
)

data class HadithSearchQuickResult(
    @ColumnInfo(name = "hadith_id") val hadithId: String,
    @ColumnInfo(name = "book_id") val bookId: String,
    @ColumnInfo(name = "collection_id") val collectionId: String,
    @ColumnInfo(name = "hadith_number") val hadithNumber: String?,
    @ColumnInfo(name = "collection_name") val collectionName: String,
    @ColumnInfo(name = "book_number") val bookNumber: String?,
    @ColumnInfo(name = "book_title") val bookTitle: String?,
    @ColumnInfo(name = "hadith_order") val hadithOrder: Int,
)

data class BookSearchQuickResult(
    @ColumnInfo(name = "book_id") val bookId: String,
    @ColumnInfo(name = "collection_id") val collectionId: String,
    @ColumnInfo(name = "book_number") val bookNumber: String?,
    @ColumnInfo(name = "collection_name") val collectionName: String,
    @ColumnInfo(name = "book_title") val bookTitle: String?,
)
