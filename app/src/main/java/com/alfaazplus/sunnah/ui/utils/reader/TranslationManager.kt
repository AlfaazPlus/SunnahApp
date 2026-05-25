package com.alfaazplus.sunnah.ui.utils.reader

import android.content.Context
import com.alfaazplus.sunnah.api.models.ResourcesVersions
import com.alfaazplus.sunnah.ui.utils.app.AppUtils
import com.alfaazplus.sunnah.ui.utils.createPath
import java.io.File

object TranslationManager {
    private const val DIR_NAME = "wbw"
    private const val MANIFEST_FILENAME = "available_wbw_info_v2.json"

    private val ROOT_DIR_PATH: String = createPath(
        AppUtils.BASE_APP_DOWNLOADED_SAVED_DATA_DIR, DIR_NAME
    )

    private fun getRootDir(context: Context): File {
        val dir = File(context.applicationContext.filesDir, ROOT_DIR_PATH)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getTempDownloadFile(context: Context, id: String): File {
        return File(getRootDir(context), "translation_${id}.tmp")
    }

    fun saveResourceVersion(
        context: Context,
        id: String,
        version: Int,
    ) {
        TranslationVersionStore(context).setItemVersion(id, version)
    }

    fun isUpdateAvailable(
        context: Context,
        id: String,
        remoteVersions: ResourcesVersions,
    ): Boolean {
        val remoteVersion = remoteVersions.translations[id] ?: return false
        return remoteVersion > TranslationVersionStore(context).getItemVersion(id)
    }
}
