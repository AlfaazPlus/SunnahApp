/*
 * Copyright (c) Faisal Khan (https://github.com/faisalcodes)
 * Created on 1/3/2022.
 * All rights reserved.
 */
package com.alfaazplus.sunnah.ui.utils.app

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.alfaazplus.sunnah.BuildConfig
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.api.JsonHelper
import com.alfaazplus.sunnah.api.RetrofitInstance
import com.alfaazplus.sunnah.api.models.AppUpdate
import com.alfaazplus.sunnah.ui.utils.createFile
import com.alfaazplus.sunnah.ui.utils.getOtherDirectory
import com.alfaazplus.sunnah.ui.utils.readFileText
import com.alfaazplus.sunnah.ui.utils.writeFileText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class UpdateBannerDecision(
    val priority: Int,
    val showCriticalDialog: Boolean,
    val showMajorDialog: Boolean,
    val showInlineBanner: Boolean,
)

class UpdateManager private constructor(private val ctx: Context) {
    companion object {
        const val CRITICAL = 5
        const val MAJOR = 4
        const val MODERATE = 3
        const val MINOR = 2
        const val COSMETIC = 1
        const val NONE = 0

        fun getInstance(context: Context): UpdateManager {
            return UpdateManager(context)
        }
    }

    private val mIconAnimationHandler = Handler(Looper.getMainLooper())
    private var mIconAnimators = ArrayList<ObjectAnimator>()

    private val _bannerDecision = MutableStateFlow(getBannerDecision())
    val bannerDecision: StateFlow<UpdateBannerDecision> = _bannerDecision.asStateFlow()

    init {
        refreshAppUpdatesJson()
    }

    fun refreshAppUpdatesJson() {
        CoroutineScope(Dispatchers.IO).launch { fetchAndSaveUpdates() }
    }

    private suspend fun fetchAndSaveUpdates() {
        withContext(Dispatchers.IO) {
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
        }

        _bannerDecision.value = getBannerDecision()

        ResourceUpdateManager
            .checkAndPerformUpdates()
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

    fun onPause() {
        mIconAnimators.forEach { it.cancel() }
        mIconAnimationHandler.removeCallbacksAndMessages(null)
    }

    fun onResume() {
        mIconAnimators.forEach { it.start() }
    }
}
