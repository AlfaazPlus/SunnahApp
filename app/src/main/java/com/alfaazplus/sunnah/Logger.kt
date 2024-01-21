package com.alfaazplus.sunnah

import android.util.Log

object Logger {
    private const val TAG = "SunnahAppLogs"

    private fun prepareMessage(vararg messages: Any?): String {
        val sb = StringBuilder()

        val trc = Thread.currentThread().stackTrace[4]
        var className = trc.className
        className = className.substring(className.lastIndexOf(".") + 1)
        sb.append("(")
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
        Log.d(TAG, prepareMessage(*messages))
    }

    fun e(e: Throwable, vararg messages: Any?) {
        Log.e(TAG, prepareMessage(*messages), e)
    }
}
