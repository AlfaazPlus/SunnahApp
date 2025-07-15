package com.alfaazplus.sunnah.ui.utils.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.receivers.CrashReceiver

object NotificationUtils {
    const val CHANNEL_ID_DEFAULT = "default"
    private const val CHANNEL_NAME_DEFAULT = "Default Channel"
    private const val CHANNEL_DESC_DEFAULT = "Miscellaneous notifications"

    const val CHANNEL_ID_HOTD = "hotd"
    private const val CHANNEL_NAME_HOTD = "Hadith of The Day"
    private const val CHANNEL_DESC_HOTD = "Daily hadith reminder notifications"

    const val CHANNEL_ID_DOWNLOADS = "downloads"
    private const val CHANNEL_NAME_DOWNLOADS = "Downloads"
    private const val CHANNEL_DESC_DOWNLOADS = "Notifications for downloads"


    fun createNotificationChannels(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx
                .getSystemService(NotificationManager::class.java)
                .apply {
                    createNotificationChannel(getDefaultChannel())
                    createNotificationChannel(getHOTDChannel())
                    createNotificationChannel(createDownloadsChannel())
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getDefaultChannel(): NotificationChannel {
        return createChannel(
            CHANNEL_ID_DEFAULT, CHANNEL_NAME_DEFAULT, CHANNEL_DESC_DEFAULT
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getHOTDChannel(): NotificationChannel {
        return createChannel(
            CHANNEL_ID_HOTD, CHANNEL_NAME_HOTD, CHANNEL_DESC_HOTD
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createDownloadsChannel(): NotificationChannel {
        return createChannel(
            CHANNEL_ID_DOWNLOADS, CHANNEL_NAME_DOWNLOADS, CHANNEL_DESC_DOWNLOADS
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String, desc: String): NotificationChannel {
        return NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = desc
            lightColor = Color.GREEN
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            vibrationPattern = longArrayOf(500, 500)

            enableLights(true)
            setShowBadge(true)
            enableVibration(true)

            setSound(
                Settings.System.DEFAULT_NOTIFICATION_URI,
                     AudioAttributes
                         .Builder()
                         .apply {
                             setUsage(AudioAttributes.USAGE_NOTIFICATION)
                             setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                         }
                         .build())
        }
    }

    fun createEmptyNotification(ctx: Context, channelId: String): Notification {
        return NotificationCompat
            .Builder(ctx, channelId)
            .apply {
                setContentTitle("")
                setSmallIcon(R.drawable.logo_icon)
                setContentText("")
            }
            .build()
    }

    fun showCrashNotification(ctx: Context, stackTraceString: String) {
        var flag = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = flag or PendingIntent.FLAG_IMMUTABLE
        }

        val copyIntent = Intent(ctx, CrashReceiver::class.java).apply {
            action = CrashReceiver.CRASH_ACTION_COPY_LOG
            putExtra(Intent.EXTRA_TEXT, stackTraceString)
        }

        val notification = NotificationCompat
            .Builder(ctx, CHANNEL_ID_DEFAULT)
            .apply {
                setContentTitle(ctx.getString(R.string.last_crash_log))
                setSmallIcon(R.drawable.logo_icon)
                setContentText(stackTraceString)
                setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(stackTraceString)
                )
                addAction(
                    NotificationCompat.Action
                        .Builder(
                            R.drawable.ic_clipboard, ctx.getString(R.string.copy), PendingIntent.getBroadcast(ctx, 0, copyIntent, flag)
                        )
                        .build()
                )
            }
            .build()

        ContextCompat
            .getSystemService(ctx, NotificationManager::class.java)
            ?.notify(CrashReceiver.NOTIFICATION_ID_CRASH, notification)
    }
}