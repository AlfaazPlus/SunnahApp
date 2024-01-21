package com.alfaazplus.sunnah.helpers

import android.content.Context
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.db.AppDatabase
import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.db.models.hadith.entities.HBookInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapterInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollectionInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPHadithConfigs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object DatabaseHelper {
    suspend fun populateHadithDataFromAssets(database: AppDatabase, context: Context) {
        context.assets.open("prebuilt-hadiths/bukhari/base.zip").use {
            importHadithBaseData(database, it)
        }
        context.assets.open("prebuilt-hadiths/bukhari/en.zip").use {
            importHadithLocaleData(database, it)
        }
        SPHadithConfigs.setAssetHadithsImported(context, true)
    }

    /**
     * [HCollection] -> 1_collection.jsontxt
     * [HBook] -> 2_books.jsontxt
     * [HChapter] -> 3_chapters.jsontxt
     * [Hadith] -> 4_hadiths.jsontxt
     */
    private suspend fun importHadithBaseData(database: AppDatabase, inputStream: InputStream) {
        val zipInputStream = ZipInputStream(inputStream)

        var zipEntry: ZipEntry?

        while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
            when (zipEntry?.name) {
                "1_collection.jsontxt" -> importCollectionBase(database.hadithDao, zipInputStream)
                "2_books.jsontxt" -> importBookBase(database.hadithDao, zipInputStream)
                "3_chapters.jsontxt" -> importChapterBase(database.hadithDao, zipInputStream)
                "4_hadiths.jsontxt" -> importHadithBase(database.hadithDao, zipInputStream)
            }
        }

        zipInputStream.close()
    }

    /**
     * [HCollectionInfo] -> 1_collection.jsontxt
     * [HBookInfo] -> 2_books.jsontxt
     * [HChapterInfo] -> 3_chapters.jsontxt
     * [HadithTranslation] -> 4_hadiths.jsontxt
     */
    private suspend fun importHadithLocaleData(database: AppDatabase, inputStream: InputStream) {
        val zipInputStream = ZipInputStream(inputStream)

        var zipEntry: ZipEntry?

        while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
            when (zipEntry?.name) {
                "1_collection.jsontxt" -> importCollectionInfo(database.hadithDao, zipInputStream)
                "2_books.jsontxt" -> importBookInfo(database.hadithDao, zipInputStream)
                "3_chapters.jsontxt" -> importChapterInfo(database.hadithDao, zipInputStream)
                "4_hadiths.jsontxt" -> importHadithTranslations(database.hadithDao, zipInputStream)
            }
        }
    }

    private suspend fun importCollectionBase(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                dao.insertCollection(toHCollection(line!!))
            }
        }
    }

    private suspend fun importCollectionInfo(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                dao.insertCollectionInfo(toHCollectionInfo(line!!))
            }
        }
    }

    private fun stringColumn(columns: JSONArray, index: Int): String {
        if (columns.isNull(index))
            return ""

        if (columns[index] is String)
            return columns[index] as String

        return columns[index].toString()
    }

    private fun doubleColumn(columns: JSONArray, index: Int): Double {
        if (columns[index] is Double)
            return columns[index] as Double

        return columns[index].toString().toDouble()
    }

    private suspend fun importBookBase(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val columns = JSONArray(line)

                dao.insertBook(
                    HBook(
                        columns[0] as Int, // id
                        columns[1] as Int, // collection_id
                        columns[2] as String, // serial_number
                        columns[3] as Int, // order_in_collection
                        columns[4] as Int, // hadith_start
                        columns[5] as Int, // hadith_end
                        columns[6] as Int, // hadith_count
                        columns[7] as String, // title
                        stringColumn(columns, 8), // intro
                        stringColumn(columns, 9), // description
                    )
                )
            }
        }
    }

    private suspend fun importBookInfo(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val columns = JSONArray(line)

                dao.insertBookInfo(
                    HBookInfo(
                        columns[0] as Int,  // id
                        columns[1] as Int,  // book_id
                        columns[2] as Int,  // collection_id
                        columns[3] as String,  // title
                        stringColumn(columns, 4),  // intro
                        stringColumn(columns, 5),  // description
                        columns[6] as String,  // language_code
                    )
                )
            }
        }
    }

    private suspend fun importChapterBase(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val columns = JSONArray(line)

                dao.insertChapter(
                    HChapter(
                        doubleColumn(columns, 0),  // id
                        columns[1] as Int,  // collection_id
                        columns[2] as Int,  // book_id
                        stringColumn(columns, 3),  // serial_number
                        stringColumn(columns, 4),  // title
                        stringColumn(columns, 5),  // intro
                        stringColumn(columns, 6),  // description
                        stringColumn(columns, 7),  // ending
                    )
                )
            }
        }
    }

    private suspend fun importChapterInfo(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val columns = JSONArray(line)

                dao.insertChapterInfo(
                    HChapterInfo(
                        columns[0] as Int,  // id
                        columns[1] as Int,  // collection_id
                        columns[2] as Int,  // book_id
                        doubleColumn(columns, 3),  // chapter_id
                        stringColumn(columns, 4),  // serial_number
                        stringColumn(columns, 5),  // title
                        stringColumn(columns, 6),  // intro
                        stringColumn(columns, 7),  // description
                        stringColumn(columns, 8),  // ending
                        columns[9] as String,  // language_code
                    )
                )
            }
        }
    }

    private suspend fun importHadithBase(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val columns = JSONArray(line)

                try {
                    dao.insertHadith(
                        Hadith(
                            columns[0] as Int,  // id
                            stringColumn(columns, 1),  // urn
                            columns[2] as Int,  // collection_id
                            columns[3] as Int,  // book_id
                            if (columns.isNull(4)) null else doubleColumn(columns, 4),  // chapter_id
                            stringColumn(columns, 5),  // hadith_number
                            columns[6] as Int,  // order_in_book
                            stringColumn(columns, 7),  // narrator_prefix
                            stringColumn(columns, 8),  // hadith_text
                            stringColumn(columns, 9),  // narrator_suffix
                            stringColumn(columns, 10),  // comments
                            stringColumn(columns, 11),  // grades
                            stringColumn(columns, 12),  // narrators
                            stringColumn(columns, 13),  // narrators2
                            stringColumn(columns, 14),  // related
                        )
                    )
                } catch (e: Exception) {
                    Logger.d("ERROR: importHadithBase: ", columns)
                    throw e
                }
            }
        }
    }

    private suspend fun importHadithTranslations(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val columns = JSONArray(line)

                dao.insertHadithTranslation(
                    HadithTranslation(
                        columns[0] as Int,  // id
                        columns[1] as Int,  // collection_id
                        stringColumn(columns, 2),  // urn
                        stringColumn(columns, 3),  // ar_urn
                        stringColumn(columns, 4),  // narrator_prefix
                        columns[5] as String,  // hadith_text
                        stringColumn(columns, 6),  // narrator_suffix
                        stringColumn(columns, 7),  // comments
                        stringColumn(columns, 8),  // grades
                        stringColumn(columns, 9),  // reference
                        stringColumn(columns, 10),  // reference_in_book
                        stringColumn(columns, 11),  // reference_usc_msa
                        stringColumn(columns, 12),  // reference_eng
                        columns[13] as String,  // language_code
                    )
                )
            }
        }
    }

    fun toHCollection(jsonText: String): HCollection {
        val columns = JSONArray(jsonText)

        return HCollection(
            columns[0] as Int, // id
            columns[1] as String, // type
            columns[2] == 1, // has_volumes
            columns[3] == 1, // has_books
            columns[4] == 1, // has_chapters
            columns[5] as String, // name
            stringColumn(columns, 6), // intro
            stringColumn(columns, 7), // description
            stringColumn(columns, 8), // numbering_source
        )
    }

    fun toHCollectionInfo(jsonText: String): HCollectionInfo {
        val columns = JSONArray(jsonText)

        return HCollectionInfo(
            columns[0] as Int, // id
            columns[1] as Int, // collection_id
            columns[2] as String, // name
            stringColumn(columns, 3), // intro
            stringColumn(columns, 4), // description
            stringColumn(columns, 5), // numbering_source
            columns[6] as String // language_code
        )
    }
}