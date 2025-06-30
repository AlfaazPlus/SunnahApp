package com.alfaazplus.sunnah.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.alfaazplus.sunnah.Logger

private fun tryLaunchIntent(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Logger.saveError(e, "tryLaunchIntent")
        e.printStackTrace()
    }
}

fun browseLink(context: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = link.toUri()
    tryLaunchIntent(context, intent)
}