package com.alfaazplus.sunnah.ui.helpers

import android.content.Context
import android.content.Intent
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.api.ApiConfig
import com.alfaazplus.sunnah.ui.models.QuranReference
import com.alfaazplus.sunnah.ui.utils.browseLink

object NavigationHelper {
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.alfaazplus.sunnah"

    /**
     * Open Quran reference in the QuranApp if installed, otherwise throw/prompt the user to install it.
     */
    fun openQuranReference(context: Context, reference: QuranReference) {
        try {
            val intent = Intent(QuranAppContract.ACTION_SHOW_POPUP).apply {
                putExtra(QuranAppContract.EXTRA_CHAPTER_NUMBER, reference.chapter)

                if (reference.isSingleVerse()) {
                    putExtra(QuranAppContract.EXTRA_VERSES, "${reference.fromVerse}")
                } else {
                    putExtra(QuranAppContract.EXTRA_VERSES, "${reference.fromVerse}-${reference.toVerse}")
                }
            }

            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(QuranAppContract.ACTION_OPEN_READER).apply {
                putExtra(QuranAppContract.EXTRA_CHAPTER_NUMBER, reference.chapter)
                if (reference.isSingleVerse()) {
                    putExtra(QuranAppContract.EXTRA_VERSE_NO, reference.fromVerse)
                } else {
                    putExtra(QuranAppContract.EXTRA_VERSES, intArrayOf(reference.fromVerse, reference.toVerse))
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun openGithubRepo(context: Context) {
        browseLink(context, ApiConfig.GITHUB_REPOSITORY_URL)
    }

    fun openGithubIssuesHadithReport(context: Context) {
        browseLink(context, ApiConfig.GITHUB_ISSUES_HADITH_REPORT_URL)
    }

    fun openPlayStoreListing(context: Context) {
        browseLink(context, PLAY_STORE_URL)
    }

    fun openQuranAppPlayStoreListing(context: Context) {
        browseLink(context, "https://play.google.com/store/apps/details?id=com.quranapp.android")
    }

    fun openAboutUs(context: Context) {
        browseLink(context, "https://sunnah.alfaazplus.com/about")
    }

    fun openPrivacyPolicy(context: Context) {
        browseLink(context, "https://sunnah.alfaazplus.com/privacy")
    }

    fun openDonationPage(context: Context) {
        browseLink(context, "https://donate.alfaazplus.com?ref=sunnahapp")
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Logger.saveError(e, "openAppSettings")
        }
    }

    fun shareApp(context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.share_app_msg, PLAY_STORE_URL),
            )
        }

        context.startActivity(
            Intent.createChooser(intent, "Share via")
        )
    }
}

object QuranAppContract {
    const val PACKAGE_NAME = "com.quranapp.android"
    const val POPUP_ACTIVITY = "com.quranapp.android.activities.popup.PopupQuranActivity"

    const val ACTION_SHOW_POPUP = "com.quranapp.android.action.SHOW_POPUP"
    const val ACTION_OPEN_READER = "com.quranapp.android.action.OPEN_READER"
    const val EXTRA_CHAPTER_NUMBER = "chapterNo"
    const val EXTRA_VERSE_NO = "verseNo"
    const val EXTRA_VERSES = "verses"
    const val EXTRA_TRANSLATION_SLUGS = "translationSlugs"
}
