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
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


object DatabaseHelper {
    suspend fun populateHadithDataFromAssets(database: AppDatabase, context: Context) {
        context.assets.open("prebuilt-hadiths/bukhari/base.tar.bz2").use {
            importHadithBaseData(database, it)
        }
        context.assets.open("prebuilt-hadiths/bukhari/en.tar.bz2").use {
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
    suspend fun importHadithBaseData(database: AppDatabase, inputStream: InputStream) {
        TarArchiveInputStream(BZip2CompressorInputStream(inputStream)).use { tarInput ->
            var entry = tarInput.nextEntry
            while (entry != null) {
                when (entry.name) {
                    "1_collection.jsontxt" -> importCollectionBase(database.hadithDao, tarInput)
                    "2_books.jsontxt" -> importBookBase(database.hadithDao, tarInput)
                    "3_chapters.jsontxt" -> importChapterBase(database.hadithDao, tarInput)
                    "4_hadiths.jsontxt" -> importHadithBase(database.hadithDao, tarInput)
                }
                entry = tarInput.nextEntry
            }
        }
    }

    /**
     * [HCollectionInfo] -> 1_collection.jsontxt
     * [HBookInfo] -> 2_books.jsontxt
     * [HChapterInfo] -> 3_chapters.jsontxt
     * [HadithTranslation] -> 4_hadiths.jsontxt
     */
    suspend fun importHadithLocaleData(database: AppDatabase, inputStream: InputStream) {
        TarArchiveInputStream(BZip2CompressorInputStream(inputStream)).use { tarInput ->
            var entry = tarInput.nextEntry
            while (entry != null) {
                when (entry.name) {
                    "1_collection.jsontxt" -> importCollectionInfo(database.hadithDao, tarInput)
                    "2_books.jsontxt" -> importBookInfo(database.hadithDao, tarInput)
                    "3_chapters.jsontxt" -> importChapterInfo(database.hadithDao, tarInput)
                    "4_hadiths.jsontxt" -> importHadithTranslations(database.hadithDao, tarInput)
                }
                entry = tarInput.nextEntry
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
        if (columns.isNull(index)) return ""

        if (columns[index] is String) return columns[index] as String

        return columns[index].toString()
    }

    private fun doubleColumn(columns: JSONArray, index: Int): Double {
        if (columns[index] is Double) return columns[index] as Double

        return columns[index].toString().toDouble()
    }

    private suspend fun importBookBase(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var lineNum = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                lineNum++
                val columns = JSONArray(line)

                try {
                    dao.insertBook(
                        HBook(
                            id = columns[0] as Int,
                            collectionId = columns[1] as Int,
                            serialNumber = columns[2] as String,
                            orderInCollection = columns[3] as Int,
                            hadithStart = columns[4] as Int,
                            hadithEnd = columns[5] as Int,
                            hadithCount = columns[6] as Int,
                            title = stringColumn(columns, 7),
                            intro = stringColumn(columns, 8),
                            description = stringColumn(columns, 9),
                        )
                    )
                } catch (e: Exception) {
                    Logger.d("ERROR: importBookBase (line $lineNum): ", columns)
                    throw e
                }
            }
        }
    }

    private suspend fun importBookInfo(dao: HadithDao, stream: InputStream) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(stream))

            var lineNum = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                lineNum++
                val columns = JSONArray(line)

                try {
                    dao.insertBookInfo(
                        HBookInfo(
                            id = columns[0] as Int,
                            bookId = columns[1] as Int,
                            collectionId = columns[2] as Int,
                            title = columns[3] as String,
                            intro = stringColumn(columns, 4),
                            description = stringColumn(columns, 5),
                            languageCode = columns[6] as String,
                        )
                    )
                } catch (e: Exception) {
                    Logger.d("ERROR: importBookInfo (line $lineNum): ", columns)
                    throw e
                }
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
                        id = doubleColumn(columns, 0),
                        collectionId = columns[1] as Int,
                        bookId = columns[2] as Int,
                        serialNumber = stringColumn(columns, 3),
                        title = stringColumn(columns, 4),
                        intro = stringColumn(columns, 5),
                        description = stringColumn(columns, 6),
                        ending = stringColumn(columns, 7),
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
                        id = columns[0] as Int,
                        collectionId = columns[1] as Int,
                        bookId = columns[2] as Int,
                        chapterId = doubleColumn(columns, 3),
                        serialNumber = stringColumn(columns, 4),
                        title = stringColumn(columns, 5),
                        intro = stringColumn(columns, 6),
                        description = stringColumn(columns, 7),
                        ending = stringColumn(columns, 8),
                        languageCode = columns[9] as String,
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
                            id = columns[0] as Int,
                            urn = stringColumn(columns, 1),
                            collectionId = columns[2] as Int,
                            bookId = columns[3] as Int,
                            chapterId = if (columns.isNull(4)) null else doubleColumn(columns, 4),
                            hadithNumber = stringColumn(columns, 5),
                            orderInBook = columns[6] as Int,
                            hadithPrefix = stringColumn(columns, 7),
                            hadithText = stringColumn(columns, 8),
                            hadithSuffix = stringColumn(columns, 9),
                            comments = stringColumn(columns, 10),
                            grades = stringColumn(columns, 11),
                            gradedBy = stringColumn(columns, 12),
                            narrators = stringColumn(columns, 13),
                            narrators2 = stringColumn(columns, 14),
                            related = stringColumn(columns, 15),
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

            var lineNum = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                lineNum++
                val columns = JSONArray(line)

                try {
                    dao.insertHadithTranslation(
                        HadithTranslation(
                            id = columns[0] as Int,
                            collectionId = columns[1] as Int,
                            urn = stringColumn(columns, 2),
                            arUrn = stringColumn(columns, 3),
                            narratorPrefix = stringColumn(columns, 4),
                            hadithText = stringColumn(columns, 5),
                            narratorSuffix = stringColumn(columns, 6),
                            comments = stringColumn(columns, 7),
                            grades = stringColumn(columns, 8),
                            gradedBy = stringColumn(columns, 9),
                            reference = stringColumn(columns, 10),
                            refInBook = stringColumn(columns, 11),
                            refUscMsa = stringColumn(columns, 12),
                            refEn = stringColumn(columns, 13),
                            langCode = columns[14] as String,
                        )
                    )
                } catch (e: Exception) {
                    Logger.d("ERROR: importHadithTranslations (line $lineNum): ", columns)
                    throw e
                }
            }
        }
    }

    fun toHCollection(jsonText: String): HCollection {
        val columns = JSONArray(jsonText)

        return HCollection(
            id = columns[0] as Int,
            type = columns[1] as String,
            hasVolumes = columns[2] == 1,
            hasBooks = columns[3] == 1,
            hasChapters = columns[4] == 1,
            name = columns[5] as String,
            intro = stringColumn(columns, 6),
            description = stringColumn(columns, 7),
            numberingSource = stringColumn(columns, 8),
        )
    }

    fun toHCollectionInfo(jsonText: String): HCollectionInfo {
        val columns = JSONArray(jsonText)

        return HCollectionInfo(
            id = columns[0] as Int,
            collectionId = columns[1] as Int,
            name = columns[2] as String,
            intro = stringColumn(columns, 3),
            description = stringColumn(columns, 4),
            numberingSource = stringColumn(columns, 5),
            languageCode = columns[6] as String
        )
    }
}