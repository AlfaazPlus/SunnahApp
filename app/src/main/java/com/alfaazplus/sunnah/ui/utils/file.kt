package com.alfaazplus.sunnah.ui.utils

import com.alfaazplus.sunnah.SunnahApp
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.StringJoiner
import kotlin.text.Charsets


val APP_DOWNLOADED_DATA_DIR: String = createPath("downloaded", "saved_data")
const val APP_LOG_DATA_DIR = "logs"
val APP_OTHER_DIR: String = createPath(APP_DOWNLOADED_DATA_DIR, "other")

fun createPath(vararg subPaths: String): String {
    val joiner = StringJoiner(File.separator)

    for (child in subPaths) {
        if (child.isNotEmpty()) {
            joiner.add(child)
        }
    }

    return joiner.toString()
}

fun makeAndGetAppResourceDir(dirName: String): File? {
    val file = File(SunnahApp.appFilesDir, dirName)

    if (file.exists()) return file

    if (!file.mkdirs()) return null

    return file
}

fun getOtherDirectory(): File? {
    return makeAndGetAppResourceDir(APP_OTHER_DIR)
}

fun File.readFileText(charset: Charset = Charsets.UTF_8): String =
    inputStream().bufferedReader(charset).use { it.readText() }

fun File.writeFileText(text: String, charset: Charset = Charsets.UTF_8) {
    outputStream().bufferedWriter(charset).use { it.write(text) }
}

fun File.createFile(): Boolean {
    if (exists()) {
        return true
    }

    try {
        if (parentFile != null) {
            parentFile?.mkdirs()
            return createNewFile()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return false
}


fun File.isGzip(): Boolean {
    inputStream().use { input ->
        val buffered = input.buffered()

        buffered.mark(2)

        val byte1 = buffered.read()
        val byte2 = buffered.read()

        buffered.reset()

        return byte1 == 0x1f && byte2 == 0x8b
    }
}
