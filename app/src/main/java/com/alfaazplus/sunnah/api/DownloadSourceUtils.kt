package com.alfaazplus.sunnah.api

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager

object DownloadSourceUtils {
    const val DOWNLOAD_SRC_ALFAAZ_PLUS = "alfaazplus"
    const val DOWNLOAD_SRC_GITHUB = "github"
    const val DOWNLOAD_SRC_JSDELIVR = "jsdelivr"
    const val DOWNLOAD_SRC_DEFAULT = DOWNLOAD_SRC_ALFAAZ_PLUS

    fun getDownloadSourceName(src: String): String {
        return when (src) {
            DOWNLOAD_SRC_GITHUB -> "raw.githubusercontent.com"
            DOWNLOAD_SRC_JSDELIVR -> "cdn.jsdelivr.net"
            else -> "gh-proxy.alfaazplus.com"
        }
    }

    @Composable
    fun observeCurrentDownloadSourceName(): String {
        val src = observeResourceDownloadSrc()

        return getDownloadSourceName(src)
    }

    fun getDownloadSourceBaseUrl(): String {
        val src = DataStoreManager.read(stringPreferencesKey(Keys.RESOURCE_DOWNLOAD_SRC), DOWNLOAD_SRC_DEFAULT)

        return when (src) {
            DOWNLOAD_SRC_GITHUB -> ApiConfig.GITHUB_ROOT_URL
            DOWNLOAD_SRC_JSDELIVR -> ApiConfig.JS_DELIVR_ROOT_URL
            else -> ApiConfig.GH_PROXY_ROOT_URL
        }
    }

    fun resetDownloadSourceBaseUrl() {
        val downloadSrcUrl = getDownloadSourceBaseUrl()

        if (RetrofitInstance.githubResDownloadUrl != downloadSrcUrl) {
            RetrofitInstance.githubResDownloadUrl = downloadSrcUrl
            RetrofitInstance.resetGithubApi()
        }
    }

    @Composable
    fun observeResourceDownloadSrc(): String {
        return DataStoreManager.observe(stringPreferencesKey(Keys.RESOURCE_DOWNLOAD_SRC), DOWNLOAD_SRC_DEFAULT)
    }

    suspend fun setResourceDownloadSrc(src: String) {
        DataStoreManager.write(stringPreferencesKey(Keys.RESOURCE_DOWNLOAD_SRC), src)
        resetDownloadSourceBaseUrl()
    }
}