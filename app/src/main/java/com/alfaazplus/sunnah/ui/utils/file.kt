package com.alfaazplus.sunnah.ui.utils

import com.alfaazplus.sunnah.SunnahApp
import java.io.File
import kotlin.io.path.Path


val APP_DOWNLOADED_DATA_DIR: String = Path("downloaded", "saved_data").toString()
const val APP_LOG_DATA_DIR = "logs"
val APP_OTHER_DIR: String = Path(APP_DOWNLOADED_DATA_DIR, "other").toString()

fun makeAndGetAppResourceDir(dirName: String): File? {
    val file = File(SunnahApp.appFilesDir, dirName)

    if (file.exists()) return file

    if (!file.mkdirs()) return null

    return file
}