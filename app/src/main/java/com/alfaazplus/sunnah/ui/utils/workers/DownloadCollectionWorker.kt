package com.alfaazplus.sunnah.ui.utils.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.api.RetrofitInstance
import com.alfaazplus.sunnah.db.AppDatabase
import com.alfaazplus.sunnah.helpers.DatabaseHelper
import com.alfaazplus.sunnah.ui.utils.extension.getContentLengthAndStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DownloadCollectionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val database: AppDatabase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val collectionId = inputData.getInt("collectionId", -1)

        if (collectionId == -1) return@withContext Result.failure()


        try {
            val (_, baseByteStream) = RetrofitInstance.github.getCollection(collectionId).getContentLengthAndStream()
            DatabaseHelper.importHadithBaseData(database, baseByteStream)


            val (_, localeByteStream) = RetrofitInstance.github.getCollectionTranslation(collectionId, "en").getContentLengthAndStream()
            DatabaseHelper.importHadithLocaleData(database, localeByteStream)

            return@withContext Result.success()
        } catch (e: Exception) {
            database.hadithDao.deleteCollection(collectionId)

            e.printStackTrace()
            return@withContext Result.failure()
        }
    }

}
