package com.alfaazplus.sunnah.api

import android.content.Context
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppConfigs

object DownloadSourceUtils {
    const val DOWNLOAD_SRC_GITHUB = "github"
    const val DOWNLOAD_SRC_JSDELIVR = "jsdelivr"
    const val DOWNLOAD_SRC_DEFAULT = DOWNLOAD_SRC_GITHUB

    @JvmStatic
    fun getCurrentSourceName(context: Context): String {
        return when (SPAppConfigs.getResourceDownloadSrc(context)) {
            DOWNLOAD_SRC_GITHUB -> "raw.githubusercontent.com"
            DOWNLOAD_SRC_JSDELIVR -> "cdn.jsdelivr.net"
            else -> "cdn.jsdelivr.net"
        }
    }

    @JvmStatic
    fun getDownloadSourceBaseUrl(context: Context): String {
        return when (SPAppConfigs.getResourceDownloadSrc(context)) {
            DOWNLOAD_SRC_GITHUB -> ApiConfig.GITHUB_ROOT_URL
            DOWNLOAD_SRC_JSDELIVR -> ApiConfig.JS_DELIVR_ROOT_URL
            else -> ApiConfig.JS_DELIVR_ROOT_URL
        }
    }

    @JvmStatic
    fun resetDownloadSourceBaseUrl(ctx: Context) {
        val downloadSrcUrl = getDownloadSourceBaseUrl(ctx)

        if (RetrofitInstance.githubResDownloadUrl != downloadSrcUrl) {
            RetrofitInstance.githubResDownloadUrl = downloadSrcUrl
            RetrofitInstance.resetGithubApi()
        }
    }

    fun changeDownloadSource(ctx: Context) {
        val currentSrc = SPAppConfigs.getResourceDownloadSrc(ctx)
        val newSrc = if (currentSrc == DOWNLOAD_SRC_GITHUB) DOWNLOAD_SRC_JSDELIVR else DOWNLOAD_SRC_GITHUB

        SPAppConfigs.setResourceDownloadSrc(ctx, newSrc)
        resetDownloadSourceBaseUrl(ctx)
    }
}