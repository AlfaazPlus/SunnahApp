package com.alfaazplus.sunnah.helpers

import android.content.Context
import android.content.res.AssetManager
import androidx.room3.withWriteTransaction
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.db.databases.HadithDatabaseLegacy
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.deliverable.v1.CorpusBundle
import com.alfaazplus.sunnah.ui.utils.preferences.AppPreferences
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.util.zip.GZIPInputStream


private fun isGzipPayload(bytes: ByteArray): Boolean = bytes.size >= 2 && (bytes[0].toInt() and 0xff) == 0x1f && (bytes[1].toInt() and 0xff) == 0x8b

private fun AssetManager.readCorpusAssetBytes(collectionId: String): Pair<String, ByteArray>? {
    val base = "prebuilt-hadiths/$collectionId"

    for (name in listOf("corpus.pb.gz", "corpus.pb")) {
        val path = "$base/$name"

        try {
            open(path).use { return path to it.readBytes() }
        } catch (_: FileNotFoundException) {
        }
    }

    return null
}

object DatabaseHelper {
    suspend fun populateHadithDataFromAssets(context: Context, database: HadithDatabase) {
        val startTime = System.currentTimeMillis()
        Logger.d("Hadith v2 corpus import started")

        var importedAny = false
        val assets = context.applicationContext.assets

        withContext(Dispatchers.IO) {
            for (collectionId in HadithHelper.INCLUDED_COLLECTIONS) {
                val (assetPath, rawBytes) = assets.readCorpusAssetBytes(collectionId) ?: run {
                    Logger.d("DatabaseHelperV2: no corpus asset under prebuilt-hadiths/$collectionId")
                    continue
                }

                val protoBytes = try {
                    if (isGzipPayload(rawBytes)) {
                        GZIPInputStream(ByteArrayInputStream(rawBytes)).use { it.readBytes() }
                    } else {
                        rawBytes
                    }
                } catch (e: Exception) {
                    Logger.d("DatabaseHelperV2: gzip decode failed for $assetPath: ${e.message}")
                    continue
                }

                val bundle = try {
                    CorpusBundle.parseFrom(protoBytes)
                } catch (e: InvalidProtocolBufferException) {
                    Logger.d("DatabaseHelperV2: invalid protobuf for $assetPath: ${e.message}")
                    continue
                }

                if (bundle.corpusId.isNotBlank() && bundle.corpusId != collectionId) {
                    Logger.d(
                        "DatabaseHelperV2: corpus_id mismatch for $assetPath (folder=$collectionId, bundle=${bundle.corpusId})"
                    )
                }

                val payload = bundle.toImportPayloadOrNull() ?: continue

                database.withWriteTransaction {
                    database.importDao.deleteCollectionData(collectionId)
                    database.insertCorpusImportPayload(payload)
                }

                importedAny = true
            }
        }

        if (importedAny) {
            AppPreferences.setAssetHadithsImportedVersion(HadithHelper.PREBUILT_HADITHS_VERSION)
            Logger.d("Hadith v2 corpus import finished in ${System.currentTimeMillis() - startTime} ms")
        } else {
            Logger.d("Hadith v2 corpus import skipped (no corpus assets loaded)")
        }
    }

    suspend fun cleanupLegacyHadithData(legacy: HadithDatabaseLegacy) {
        for (id in getLegacyIncludedCollectionIds()) {
            legacy.hadithDao.deleteCollection(id)
        }
        Logger.d("DatabaseHelper: legacy hadith collections deleted")
    }
}

fun getLegacyIncludedCollectionIds(): List<Int> {
    /**
     * There were 6 legacy collections with integer Ids.
     * Mapped in order:
     * bukhari, muslim, nasai, abudawud, tirmidhi, ibnmajah
     */
    return (1..6).toList()
}
