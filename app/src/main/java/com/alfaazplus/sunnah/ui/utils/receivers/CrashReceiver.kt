package com.alfaazplus.sunnah.ui.utils.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.api.ApiConfig
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPLog

class CrashReceiver : BroadcastReceiver() {
    companion object {
        const val CRASH_ACTION_COPY_LOG = "quranapp.action.crash.COPY_LOG"
        const val CRASH_ACTION_CREATE_ISSUE = "quranapp.action.crash.CREATE_ISSUE"
        const val NOTIFICATION_ID_CRASH = 1012
    }

    override fun onReceive(context: Context, intent: Intent?) {
        ContextCompat
            .getSystemService(context, NotificationManager::class.java)
            ?.cancel(NOTIFICATION_ID_CRASH)

        if (intent?.action == CRASH_ACTION_COPY_LOG || intent?.action == CRASH_ACTION_CREATE_ISSUE) {
            context.copyToClipboard(intent.getStringExtra(Intent.EXTRA_TEXT)!!)
            SPLog.removeLastCrashLogFilename(context)
        }

        when (intent?.action) {
            CRASH_ACTION_COPY_LOG -> {
                MessageUtils.showClipboardMessage(context, context.getString(R.string.copied_to_clipboard))
            }

            CRASH_ACTION_CREATE_ISSUE -> {
                Toast
                    .makeText(context, R.string.paste_log_github_issue, Toast.LENGTH_LONG)
                    .show()

                context.startActivity(Intent(Intent.ACTION_VIEW, ApiConfig.GITHUB_ISSUES_BUG_REPORT_URL.toUri()).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }
}