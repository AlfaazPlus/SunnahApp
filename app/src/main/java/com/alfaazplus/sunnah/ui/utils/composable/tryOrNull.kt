package com.alfaazplus.sunnah.ui.utils.composable

suspend fun <T> tryOrNull(block: suspend () -> T): T? {
    return try {
        block()
    } catch (_: Exception) {
        null
    }
}
