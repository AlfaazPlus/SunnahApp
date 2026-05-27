package com.alfaazplus.sunnah.ui.utils.extended

import android.content.Context
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils

class ExceptionHandler(
    private val ctx: Context,
) : Thread.UncaughtExceptionHandler {
    private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, exc: Throwable) {
        Logger.saveCrash(ctx, exc)
        NotificationUtils.showCrashNotification(ctx, exc.stackTraceToString())
        defaultExceptionHandler?.uncaughtException(thread, exc)
    }
}