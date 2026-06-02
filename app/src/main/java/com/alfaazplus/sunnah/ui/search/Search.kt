package com.alfaazplus.sunnah.ui.search

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.text.AnnotatedString
import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.alfaazplus.sunnah.db.entities.v2.BookEntity



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

data class SearchIndexSourceRow(
    @ColumnInfo(name = "hadith_id") val hadithId: String,
    @ColumnInfo(name = "collection_id") val collectionId: String,
    @ColumnInfo(name = "lang_code") val langCode: String,
    @ColumnInfo(name = "blocks_json") val blocksJson: String,
)

data class SearchIndexMatchRow(
    @ColumnInfo(name = "hadith_id") val hadithId: String,
    @ColumnInfo(name = "matched_lang") val matchedLang: String,
    @ColumnInfo(name = "matched_text") val matchedText: String,
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
    @ColumnInfo(name = "lang_code") val langCode: String,
    @ColumnInfo(name = "title") val title: String?,
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
