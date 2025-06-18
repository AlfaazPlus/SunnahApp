package com.alfaazplus.sunnah.ui.models

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.alfaazplus.sunnah.db.contracts.BookContract
import com.alfaazplus.sunnah.db.contracts.BookInfoContract
import com.alfaazplus.sunnah.db.contracts.CollectionInfoContract
import com.alfaazplus.sunnah.db.contracts.HadithContract
import com.alfaazplus.sunnah.db.contracts.HadithTranslationContract
import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.db.models.hadith.entities.HBookInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation
import com.alfaazplus.sunnah.db.models.scholars.Scholar

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
    save = {
        listOf(it.type)
    },
    restore = {
        SearchResultTab.fromType(it[0])
    },
)

data class HadithSearchResult(
    @Embedded val hadith: Hadith,
    @Relation(
        parentColumn = HadithContract.Columns.URN,
        entityColumn = HadithTranslationContract.Columns.AR_URN,
    ) val translation: HadithTranslation,

    @ColumnInfo(name = CollectionInfoContract.Columns.NAME) val collectionName: String,
) {
    @Ignore
    var translationText: AnnotatedString = buildAnnotatedString { }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HadithSearchResult) return false

        if (hadith.urn != other.hadith.urn) return false
        if (hadith.collectionId != other.hadith.collectionId) return false
        if (hadith.bookId != other.hadith.bookId) return false
        if (translation.langCode != other.translation.langCode) return false
        if (translationText != other.translationText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hadith.hashCode()
        result = 31 * result + translation.hashCode()
        result = 31 * result + collectionName.hashCode()
        result = 31 * result + translationText.hashCode()
        return result
    }
}

data class BooksSearchResult(
    @Embedded val book: HBook,
    @Relation(
        parentColumn = BookContract.Columns.ID,
        entityColumn = BookInfoContract.Columns.BOOK_ID,
    ) val info: HBookInfo,

    @ColumnInfo(name = CollectionInfoContract.Columns.NAME) val collectionName: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BooksSearchResult) return false

        if (info.bookId != other.info.bookId) return false
        if (info.collectionId != other.info.collectionId) return false
        if (info.languageCode != other.info.languageCode) return false
        if (book.id != other.book.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = book.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + collectionName.hashCode()
        return result
    }
}

data class ScholarsSearchResult(
    val scholars: List<Scholar>,
)

data class HadithSearchQuickResult(
    @ColumnInfo(name = CollectionInfoContract.Columns.NAME) val collectionName: String,
    @ColumnInfo(name = BookInfoContract.Columns.TITLE) val bookTitle: String,
    @ColumnInfo(name = HadithContract.Columns.HADITH_NUMBER) val hadithNumber: String,
    @ColumnInfo(name = HadithContract.Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = HadithContract.Columns.BOOK_ID) val bookId: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HadithSearchQuickResult) return false

        if (collectionName != other.collectionName) return false
        if (bookTitle != other.bookTitle) return false
        if (hadithNumber != other.hadithNumber) return false
        if (collectionId != other.collectionId) return false
        if (bookId != other.bookId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = collectionName.hashCode()
        result = 31 * result + bookTitle.hashCode()
        result = 31 * result + hadithNumber.hashCode()
        result = 31 * result + collectionId
        result = 31 * result + bookId
        return result
    }
}

data class BookSearchQuickResult(
    @ColumnInfo(name = CollectionInfoContract.Columns.NAME) val collectionName: String,
    @ColumnInfo(name = BookInfoContract.Columns.TITLE) val bookTitle: String,
    @ColumnInfo(name = BookContract.Columns.ID) val bookId: Int,
    @ColumnInfo(name = BookContract.Columns.SERIAL_NUMBER) val serialNumber: Int,
    @ColumnInfo(name = BookContract.Columns.COLLECTION_ID) val collectionId: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HadithSearchQuickResult) return false

        if (collectionName != other.collectionName) return false
        if (bookTitle != other.bookTitle) return false
        if (collectionId != other.collectionId) return false
        if (bookId != other.bookId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = collectionName.hashCode()
        result = 31 * result + bookTitle.hashCode()
        result = 31 * result + collectionId
        result = 31 * result + bookId
        return result
    }
}
