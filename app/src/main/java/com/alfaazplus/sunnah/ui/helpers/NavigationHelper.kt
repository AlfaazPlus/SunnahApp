package com.alfaazplus.sunnah.ui.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.alfaazplus.sunnah.api.ApiConfig
import com.alfaazplus.sunnah.ui.models.QuranReference

object NavigationHelper {
    /**
     * Open Quran reference in the QuranApp if installed, otherwise throw/prompt the user to install it.
     */
    fun openQuranReference(context: Context, reference: QuranReference) {
        val intent = Intent("com.quranapp.android.action.OPEN_READER").apply {
            putExtra("chapterNo", reference.chapter)
            if (reference.isSingleVerse()) {
                putExtra("verseNo", reference.fromVerse)
            } else {
                putExtra("verses", intArrayOf(reference.fromVerse, reference.toVerse))
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openGithubRepo(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ApiConfig.GITHUB_REPOSITORY_URL)))
    }

    fun openPlayStoreListing(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.alfaazplus.sunnah")))
    }

    fun openQuranAppPlayStoreListing(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.quranapp.android")))
    }

    fun openAboutUs(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sunnah.alfaazplus.com/about")))
    }

    fun openPrivacyPolicy(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sunnah.alfaazplus.com/privacy")))
    }
}