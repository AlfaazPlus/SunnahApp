package com.alfaazplus.sunnah

import android.content.Context
import android.util.Log
import com.alfaazplus.sunnah.ui.utils.APP_LOG_DATA_DIR
import com.alfaazplus.sunnah.ui.utils.getDateTimeNow
import com.alfaazplus.sunnah.ui.utils.makeAndGetAppResourceDir
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPLog
import java.io.File
import kotlin.io.path.Path

object Logger {
    private const val TAG = "SunnahAppLogs"
    const val FILE_NAME_DATE_FORMAT = "yyyyMMddHHmmssSSS"
    val CRASH_LOGS_DIR = makeAndGetAppResourceDir(Path(APP_LOG_DATA_DIR, "crashes").toString())
    val SUPPRESSED_LOGS_DIR = makeAndGetAppResourceDir(Path(APP_LOG_DATA_DIR, "suppressed_errors").toString())

    private fun prepareMessage(vararg messages: Any?): String {
        val sb = StringBuilder()

        val trc = Thread.currentThread().stackTrace[4]
        var className = trc.className
        className = className.substring(className.lastIndexOf(".") + 1)
        sb
            .append("(")
            .append(className)
            .append("=>")
            .append(trc.methodName)
            .append(":")
            .append(trc.lineNumber)
            .append("): ")

        val len = messages.size
        for (i in messages.indices) {
            val msg = messages[i]
            if (msg != null) sb.append(msg.toString()) else sb.append("null")
            if (i < len - 1) sb.append(", ")
        }

        return sb.toString()
    }

    fun d(vararg messages: Any?) {
        if (!BuildConfig.DEBUG) return
        println("$TAG: ${prepareMessage(*messages)}")
    }

    fun e(e: Throwable, vararg messages: Any?) {
        e.printStackTrace()
        println("$TAG: ${prepareMessage(*messages)}")
    }

    fun saveCrash(ctx: Context, e: Throwable?) {
        if (e == null) return
        e.printStackTrace()

        val crashDir = CRASH_LOGS_DIR ?: return

        try {
            val trc = e.stackTraceToString()
            val filename = getDateTimeNow(FILE_NAME_DATE_FORMAT)
            val logFile = File(crashDir, "$filename.txt")

            logFile.createNewFile()
            logFile.writeText(trc)

            keepLastNFiles(crashDir, 20)
            SPLog.saveLastCrashLogFileName(ctx, logFile.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun saveError(e: Throwable?, place: String) {
        if (e == null) return

        e.printStackTrace()

        val suppressedErrorDir = SUPPRESSED_LOGS_DIR ?: return

        try {
            val trc = e.stackTraceToString()
            val filename = getDateTimeNow(FILE_NAME_DATE_FORMAT)
            val logFile = File(suppressedErrorDir, "$filename@${place}.txt")

            logFile.createNewFile()
            logFile.writeText(trc)

            keepLastNFiles(suppressedErrorDir, 30)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun keepLastNFiles(dir: File, n: Int) {
        val files = dir.listFiles()

        if (files != null && files.size > n) {
            val sortedFiles = files.sortedByDescending { it.lastModified() }
            val len = sortedFiles.size - n
            for (i in 0 until len) {
                sortedFiles[i].delete()
            }
        }
    }

}
