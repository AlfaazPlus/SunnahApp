/*
 * Copyright (c) Faisal Khan (https://github.com/faisalcodes)
 * Created on 1/3/2022.
 * All rights reserved.
 */
package com.alfaazplus.sunnah.ui.utils.app

import com.alfaazplus.sunnah.BuildConfig
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.api.JsonHelper
import com.alfaazplus.sunnah.api.RetrofitInstance
import com.alfaazplus.sunnah.api.models.AppUpdate
import com.alfaazplus.sunnah.ui.utils.createFile
import com.alfaazplus.sunnah.ui.utils.getOtherDirectory
import com.alfaazplus.sunnah.ui.utils.readFileText
import com.alfaazplus.sunnah.ui.utils.writeFileText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

data class UpdateBannerDecision(
    val priority: Int,
    val showCriticalDialog: Boolean,
    val showMajorDialog: Boolean,
    val showInlineBanner: Boolean,
)

object UpdateManager {
    const val CRITICAL = 5
    const val MAJOR = 4
    const val MODERATE = 3
    const val MINOR = 2
    const val COSMETIC = 1
    const val NONE = 0

    private val _bannerDecision = MutableStateFlow(getBannerDecision())
    val bannerDecision: StateFlow<UpdateBannerDecision> = _bannerDecision.asStateFlow()

    @Volatile
    private var isRefreshing = false

    suspend fun refreshAppUpdatesJson() = withContext(Dispatchers.IO) {
        if (isRefreshing) return@withContext

        isRefreshing = true

        try {
            fetchAndSaveUpdates()
        } finally {
            isRefreshing = false
        }
    }

    private suspend fun fetchAndSaveUpdates() {
        try {
            val updates = RetrofitInstance.github.getAppUpdates()
            val updatesString = JsonHelper.json.encodeToString(updates)
            Logger.d("updatesString: $updatesString")

            val updatesFile = getAppUpdatesFile()

            if (updatesFile.createFile()) {
                updatesFile.writeFileText(updatesString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _bannerDecision.value = getBannerDecision()

        ResourceUpdateManager.checkAndPerformUpdates()
    }

    fun getBannerDecision(): UpdateBannerDecision {
        val priority = getMostImportantUpdate().priority

        return UpdateBannerDecision(
            priority = priority,
            showCriticalDialog = priority == CRITICAL,
            showMajorDialog = priority == MAJOR,
            showInlineBanner = priority in MAJOR downTo COSMETIC,
        )
    }

    private fun getMostImportantUpdate(): AppUpdate {
        val currentAppVersion = BuildConfig.VERSION_CODE.toLong()

        return getAvailableUpdates()
            .filter { it.version > currentAppVersion }
            .maxByOrNull { it.priority } ?: AppUpdate(0, NONE)
    }

    private fun getAvailableUpdates(): List<AppUpdate> {
        return try {
            val file = getAppUpdatesFile()

            if (file.exists()) {
                JsonHelper.json.decodeFromString(file.readFileText())
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getAppUpdatesFile(): File {
        return File(getOtherDirectory(), "app_updates.json")
    }
}
