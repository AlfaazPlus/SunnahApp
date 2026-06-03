package com.alfaazplus.sunnah.db.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.alfaazplus.sunnah.db.entities.search.SearchContentEntity
import com.alfaazplus.sunnah.db.entities.search.SearchIndexMetaEntity
import com.alfaazplus.sunnah.ui.search.SearchIndexMatchRow

@Dao
interface SearchIndexDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(entities: List<SearchContentEntity>)

    @Upsert
    suspend fun upsertMeta(meta: SearchIndexMetaEntity)

    @Query("SELECT fingerprint FROM search_index_meta WHERE `key` = :key")
    suspend fun getFingerprint(key: String): String?

    @Query("DELETE FROM search_fts WHERE rowid IN (SELECT id FROM search_content WHERE langCode = :langCode)")
    suspend fun deleteFtsForLang(langCode: String)

    @Query("DELETE FROM search_content WHERE langCode = :langCode")
    suspend fun deleteContentForLang(langCode: String)

    @Query("DELETE FROM search_index_meta WHERE `key` = :key")
    suspend fun deleteMeta(key: String)

    @Query("INSERT INTO search_fts(search_fts) VALUES('rebuild')")
    suspend fun rebuildFtsIndex()

    @Transaction
    suspend fun replaceLang(
        langCode: String,
        entities: List<SearchContentEntity>,
        fingerprint: String,
    ) {
        removeLang(langCode)
        if (entities.isNotEmpty()) {
            insertContent(entities)
        }
        upsertMeta(SearchIndexMetaEntity(metaKey(langCode), fingerprint))
    }

    @Transaction
    suspend fun removeLang(langCode: String) {
        deleteFtsForLang(langCode)
        deleteContentForLang(langCode)
        deleteMeta(metaKey(langCode))
    }

    @Query(
        """
        SELECT COUNT(*)
        FROM (
            SELECT c.hadith_id
            FROM search_fts
            INNER JOIN search_content AS c ON c.id = search_fts.rowid
            WHERE search_fts MATCH :matchQuery
                AND (:collectionCount = 0 OR c.collection_id IN (:collectionIds))
            GROUP BY c.hadith_id
        )
        """
    )
    suspend fun countHadithMatches(
        matchQuery: String,
        collectionIds: List<String>,
        collectionCount: Int,
    ): Int

    @Query(
        """
        SELECT hadith_id, matched_lang, matched_text
        FROM (
            SELECT
                c.hadith_id AS hadith_id,
                c.langCode AS matched_lang,
                c.text AS matched_text,
                search_fts.rank AS relevance,
                ROW_NUMBER() OVER (
                    PARTITION BY c.hadith_id
                    ORDER BY search_fts.rank,
                        CASE c.langCode
                            WHEN :displayLangCode THEN 0
                            WHEN 'en' THEN 1
                            WHEN 'ar' THEN 2
                            ELSE 3
                        END
                ) AS row_num
            FROM search_fts
            INNER JOIN search_content AS c ON c.id = search_fts.rowid
            WHERE search_fts MATCH :matchQuery
                AND (:collectionCount = 0 OR c.collection_id IN (:collectionIds))
        ) AS ranked
        WHERE row_num = 1
        ORDER BY relevance,
            CASE matched_lang
                WHEN :displayLangCode THEN 0
                WHEN 'en' THEN 1
                WHEN 'ar' THEN 2
                ELSE 3
            END,
            hadith_id
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchHadithMatches(
        matchQuery: String,
        collectionIds: List<String>,
        collectionCount: Int,
        displayLangCode: String,
        limit: Int,
        offset: Int,
    ): List<SearchIndexMatchRow>

    companion object {
        fun metaKey(langCode: String): String = "lang:$langCode"
    }
}
