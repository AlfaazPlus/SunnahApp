package com.alfaazplus.sunnah.ui.components

import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.Logger.CRASH_LOGS_DIR
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialogAction
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialogActionStyle
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.readFileText
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPLog
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPLog.getLastCrashLogFilename
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun LastCrashReportDialog() {
    var crashLog by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        val crashDir = CRASH_LOGS_DIR ?: return@LaunchedEffect

        val filename = getLastCrashLogFilename(context) ?: return@LaunchedEffect
        val logFile = File(crashDir, filename)

        if (!logFile.exists()) {
            SPLog.removeLastCrashLogFilename(context)
            return@LaunchedEffect
        }

        try {
            val logText = withContext(Dispatchers.IO) { logFile.readFileText() }

            if (logText.isNotBlank()) {
                crashLog = logText
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.saveError(e, "LastCrashReportDialog")
        }
    }

    AlertDialog(
        isOpen = crashLog != null,
        onClose = {
            crashLog = null
            SPLog.removeLastCrashLogFilename(context)
        },
        title = stringResource(R.string.last_crash_log),
        actions = listOf(
            AlertDialogAction(
                text = stringResource(R.string.close),
            ),
            AlertDialogAction(
                text = stringResource(R.string.copy),
                style = AlertDialogActionStyle.Primary,
                dismissOnClick = false,
                onClick = {
                    val log = crashLog

                    crashLog = null

                    if (log == null) {
                        return@AlertDialogAction
                    }

                    context.copyToClipboard(log)
                    MessageUtils.showClipboardMessage(context, resources.getString(R.string.copied_to_clipboard))
                },
            ),
        ),
        content = {
            if (crashLog != null) {
                Text(
                    text = crashLog!!,
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace,
                    style = typography.bodyMedium,
                )
            }
        },
    )
}
