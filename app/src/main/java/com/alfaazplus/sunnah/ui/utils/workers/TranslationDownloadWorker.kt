package com.alfaazplus.sunnah.ui.utils.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.api.fetchInventoryStreamingResponse
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.deliverable.v1.CorpusBundle
import com.alfaazplus.sunnah.helpers.insertCorpusImportPayload
import com.alfaazplus.sunnah.helpers.toImportPayloadOrNull
import com.alfaazplus.sunnah.ui.activities.MainActivity
import com.alfaazplus.sunnah.ui.utils.extension.isGzip
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils.createForegroundInfoFallback
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream

@HiltWorker
class TranslationDownloadWorker @AssistedInject constructor(
    @Assisted
    private val ctx: Context,
    @Assisted
    params: WorkerParameters,
    private val database: HadithDatabase,
) : CoroutineWorker(ctx, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val translationId = inputData.getString("id") ?: return createForegroundInfoFallback(ctx)

        return createForegroundInfo(translationId, 0)
    }

    override suspend fun doWork(): Result {
        val translationId = inputData.getString("id") ?: return Result.failure()

        setForeground(createForegroundInfo(translationId, 0))

        return try {
            downloadAndStore(translationId)
            Result.success()
        } catch (e: Exception) {
            Logger.saveError(e, "TranslationDownloadWorker")
            Result.failure(workDataOf("error" to (e.message ?: "Download failed")))
        }
    }

    private suspend fun downloadAndStore(
        translationId: String,
    ) = withContext(Dispatchers.IO) {
        val tmpFile = File(ctx.filesDir, "translation_$translationId.tmp")
        val url = "ghraw://AlfaazPlus/SunnahAppInventory/master/translations/$translationId.pb.gz"

        try {
            downloadGithubRawContentToFile(
                url = url,
                dest = tmpFile,
            ) { progress ->
                if (!isStopped) {
                    setProgressAsync(workDataOf("progress" to (progress ?: 0)))
                    setForeground(createForegroundInfo(translationId, progress))
                }
            }

            val bundle = CorpusBundle.parseFrom(decodePayload(tmpFile))
            val payload = bundle.toImportPayloadOrNull() ?: error("")

            database.withTransaction {
                // fixme delete old data for the language first
                database.importDao.deleteTranslationData(translationId)
                database.insertCorpusImportPayload(payload)
            }
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
        }
    }

    private suspend fun decodePayload(source: File): ByteArray = withContext(Dispatchers.IO) {
        if (!source.exists()) throw IOException("Source file does not exist")

        val inputFactory: () -> InputStream = if (source.isGzip()) {
            {
                GZIPInputStream(
                    source
                        .inputStream()
                        .buffered()
                )
            }
        } else {
            {
                source
                    .inputStream()
                    .buffered()
            }
        }

        inputFactory().use { stream ->
            stream.readBytes()
        }
    }

    private fun createForegroundInfo(
        translationId: String,
        progress: Int?,
    ): ForegroundInfo {
        val channelId = NotificationUtils.CHANNEL_ID_DOWNLOADS
        val builder = NotificationCompat
            .Builder(ctx, channelId)
            .apply {
                setAutoCancel(false)
                setOngoing(true)
                setShowWhen(false)
                setSmallIcon(R.drawable.logo_icon)
                setContentTitle(ctx.getString(R.string.downloading))
                setContentText(TranslationUtils.getHadithTranslationLabel(ctx))
                setCategory(NotificationCompat.CATEGORY_PROGRESS)
                setProgress(100, progress ?: 0, progress == null)
            }

        val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val activityIntent = Intent(ctx, MainActivity::class.java).apply {
            putExtra(Keys.NAV_DESTINATION, Routes.SETTINGS_TRANSLATIONS)
        }

        builder.setContentIntent(
            PendingIntent.getActivity(
                ctx,
                translationId.hashCode(),
                activityIntent,
                flag,
            )
        )

        builder.addAction(
            R.drawable.ic_x,
            ctx.getString(R.string.cancel),
            WorkManager
                .getInstance(applicationContext)
                .createCancelPendingIntent(id)
        )

        return ForegroundInfo(
            translationId.hashCode(),
            builder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }
}


suspend fun downloadGithubRawContentToFile(
    url: String,
    dest: File,
    setProgress: suspend (Int?) -> Unit,
) = withContext(Dispatchers.IO) {
    val response = fetchInventoryStreamingResponse(url)

    if (!response.isSuccessful) {
        throw IOException("Download failed: HTTP ${response.code()}")
    }

    val body = response.body() ?: throw IOException("Response body is null")
    val totalBytes = body.contentLength()
    var downloaded = 0L

    body
        .byteStream()
        .use { input ->
            dest
                .outputStream()
                .buffered()
                .use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var lastUpdateTime = 0L

                    while (true) {
                        ensureActive()

                        val bytes = input.read(buffer)
                        if (bytes <= 0) break

                        output.write(buffer, 0, bytes)
                        downloaded += bytes

                        val now = System.currentTimeMillis()
                        val isFinished = totalBytes > 0L && downloaded == totalBytes

                        if (now - lastUpdateTime >= 2000L || isFinished) {
                            lastUpdateTime = now

                            val progress = if (totalBytes > 0) {
                                ((downloaded * 100) / totalBytes).toInt()
                            } else {
                                null
                            }

                            setProgress(progress)
                        }
                    }

                    output.flush()
                }
        }
}
