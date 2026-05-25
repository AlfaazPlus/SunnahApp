package com.alfaazplus.sunnah.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.alfaazplus.sunnah.ui.models.BookSearchQuickResult
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.HadithSearchQuickResult
import com.alfaazplus.sunnah.ui.models.HadithSearchRow

@Dao
interface HadithSearchDao {
    @Query(
        """
        SELECT
            h.id AS hadith_id,
            h.book_id AS book_id,
            h.collection_id AS collection_id,
            h.number AS hadith_number,
            ct.title AS collection_name,
            (
                SELECT hc.blocks_json
                FROM hadith_contents AS hc
                WHERE hc.hadith_id = h.id
                    AND hc.blocks_json LIKE '%' || :query || '%' COLLATE NOCASE
                ORDER BY CASE hc.lang
                    WHEN :displayLangCode THEN 0
                    WHEN 'en' THEN 1
                    WHEN 'ar' THEN 2
                    ELSE 3
                END
                LIMIT 1
            ) AS blocks_json,
            (
                SELECT hc.lang
                FROM hadith_contents AS hc
                WHERE hc.hadith_id = h.id
                    AND hc.blocks_json LIKE '%' || :query || '%' COLLATE NOCASE
                ORDER BY CASE hc.lang
                    WHEN :displayLangCode THEN 0
                    WHEN 'en' THEN 1
                    WHEN 'ar' THEN 2
                    ELSE 3
                END
                LIMIT 1
            ) AS matched_lang
        FROM hadiths AS h
        INNER JOIN collection_translations AS ct
            ON h.collection_id = ct.collection_id AND ct.lang = :displayLangCode
        WHERE EXISTS (
            SELECT 1
            FROM hadith_contents AS hc
            WHERE hc.hadith_id = h.id
                AND hc.blocks_json LIKE '%' || :query || '%' COLLATE NOCASE
        )
            AND (
                COALESCE(:collectionIds, '') = ''
                OR h.collection_id IN (:collectionIds)
            )
        ORDER BY h.urn
        """
    )
    fun searchHadiths(
        query: String,
        collectionIds: List<String>?,
        displayLangCode: String,
    ): PagingSource<Int, HadithSearchRow>

    @Query(
        """
        SELECT
            b.id AS id,
            b.collection_id AS collection_id,
            b.number AS number,
            ct.title AS collection_name,
            bt_en.title AS title_en,
            bt_ar.title AS title_ar,
            (
                SELECT COUNT(*)
                FROM hadiths AS h
                WHERE h.book_id = b.id
            ) AS hadith_count
        FROM books AS b
        INNER JOIN book_translations AS bt_en
            ON b.id = bt_en.book_id AND bt_en.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON b.collection_id = ct.collection_id AND ct.lang = :langCode
        LEFT JOIN book_translations AS bt_ar
            ON b.id = bt_ar.book_id AND bt_ar.lang = 'ar'
        WHERE (
            bt_en.title LIKE '%' || :query || '%' COLLATE NOCASE
            OR bt_en.intro LIKE '%' || :query || '%' COLLATE NOCASE
            OR bt_en.notes LIKE '%' || :query || '%' COLLATE NOCASE
            OR bt_en.preamble LIKE '%' || :query || '%' COLLATE NOCASE
        )
        AND (
            COALESCE(:collectionIds, '') = ''
            OR b.collection_id IN (:collectionIds)
        )
        ORDER BY ct.title, b.number + 0
        """
    )
    fun searchBooks(
        query: String,
        collectionIds: List<String>?,
        langCode: String,
    ): PagingSource<Int, BooksSearchResult>

    @Query(
        """
        SELECT
            h.id AS hadith_id,
            h.book_id AS book_id,
            h.collection_id AS collection_id,
            h.number AS hadith_number,
            ct.title AS collection_name,
            b.number AS book_number,
            bt.title AS book_title,
            (
                SELECT COUNT(*)
                FROM hadiths AS counted
                WHERE counted.book_id = h.book_id
                    AND counted.urn <= h.urn
            ) AS hadith_order
        FROM hadiths AS h
        INNER JOIN books AS b ON h.book_id = b.id
        INNER JOIN book_translations AS bt
            ON b.id = bt.book_id AND bt.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON h.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE h.number = :hadithNumber COLLATE NOCASE
        ORDER BY h.urn
        """
    )
    suspend fun searchQuickHadithsByHadithNumber(
        hadithNumber: String,
        langCode: String,
    ): List<HadithSearchQuickResult>

    @Query(
        """
        SELECT
            h.id AS hadith_id,
            h.book_id AS book_id,
            h.collection_id AS collection_id,
            h.number AS hadith_number,
            ct.title AS collection_name,
            b.number AS book_number,
            bt.title AS book_title,
            (
                SELECT COUNT(*)
                FROM hadiths AS counted
                WHERE counted.book_id = h.book_id
                    AND counted.urn <= h.urn
            ) AS hadith_order
        FROM hadiths AS h
        INNER JOIN books AS b ON h.book_id = b.id
        INNER JOIN book_translations AS bt
            ON b.id = bt.book_id AND bt.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON h.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE b.number = :bookNumber
        ORDER BY h.urn
        LIMIT 1 OFFSET :offset
        """
    )
    suspend fun searchQuickHadithByBookOrder(
        bookNumber: String,
        offset: Int,
        langCode: String,
    ): List<HadithSearchQuickResult>

    @Query(
        """
        SELECT
            b.id AS book_id,
            b.collection_id AS collection_id,
            b.number AS book_number,
            ct.title AS collection_name,
            bt.title AS book_title
        FROM books AS b
        INNER JOIN book_translations AS bt
            ON b.id = bt.book_id AND bt.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON b.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE b.number = :bookNumber
        ORDER BY b.number + 0
        """
    )
    suspend fun searchQuickBooks(
        bookNumber: String,
        langCode: String,
    ): List<BookSearchQuickResult>
}
