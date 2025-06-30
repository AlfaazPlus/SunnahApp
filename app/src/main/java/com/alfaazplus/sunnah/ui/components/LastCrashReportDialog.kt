package com.alfaazplus.sunnah.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.Logger.CRASH_LOGS_DIR
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPLog
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPLog.getLastCrashLogFilename
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun LastCrashReportDialog() {
    var showDialog by remember { mutableStateOf(false) }
    var crashLog by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val crashDir = CRASH_LOGS_DIR ?: return@LaunchedEffect

        try {
            val filename = getLastCrashLogFilename(context) ?: return@LaunchedEffect
            val logText = withContext(Dispatchers.IO) {
                File(crashDir, filename).readText()
            }

            if (logText.isNotBlank()) {
                showDialog = true
                crashLog = logText
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.saveError(e, "LastCrashReportDialog")
        }
    }

    AlertDialog(
        isOpen = showDialog,
        onClose = {
            showDialog = false
            SPLog.removeLastCrashLogFilename(context)
        },
        title = stringResource(R.string.last_crash_log),
        cancelText = stringResource(R.string.close),
        confirmText = stringResource(R.string.copy),
        onConfirm = {
            context.copyToClipboard(crashLog)
            MessageUtils.showClipboardMessage(context, context.getString(R.string.copied_to_clipboard))
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = crashLog,
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace,
                    style = typography.bodyMedium,
                )
            }
        },
    )
}