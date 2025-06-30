package com.alfaazplus.sunnah.ui.utils.extended

import android.content.Context
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils
import org.apache.commons.lang3.exception.ExceptionUtils

class ExceptionHandler(
    private val ctx: Context,
) : Thread.UncaughtExceptionHandler {
    private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, exc: Throwable) {
        Logger.saveCrash(ctx, exc)
        NotificationUtils.showCrashNotification(ctx, ExceptionUtils.getStackTrace(exc))
        defaultExceptionHandler?.uncaughtException(thread, exc)
    }
}