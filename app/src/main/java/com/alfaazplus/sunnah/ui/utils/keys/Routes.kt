/*
 * --------------------------------------------------------------------------
 * Project: SunnahApp
 * Created by Faisal Khan (http://github.com/faisalcodes) on July 06, 2023
 *
 * Copyright (c) 2023 All rights reserved.
 * --------------------------------------------------------------------------
 */


package com.alfaazplus.sunnah.ui.utils.keys

data class SingleArgRoute(private val route: String, private val argName: String) {
    operator fun invoke() = "$route/{$argName}"

    fun arg(arg: Any) = "$route/$arg"
}

data class MultiArgRoute(private val route: String, private val argNames: List<String>) {
    operator fun invoke() = "$route?${argNames.joinToString("&") { "$it={$it}" }}"

    fun args(vararg args: Any) = "$route?${argNames.zip(args).joinToString("&") { "${it.first}=${it.second}" }}"
}

object Routes {
    const val MAIN = "main"
    const val HOME = "home"
    const val LIBRARY = "library"
    const val SEARCH = "search"
    val SETTINGS = SingleArgRoute("settings", Keys.SHOW_READER_SETTINGS_ONLY)
    const val SETTINGS_THEME = "settings_theme"
    const val SETTINGS_LANGUAGE = "settings_language"
    const val SETTINGS_MANAGE_COLLECTIONS = "settings_manage_collections"
    val BOOKS_INDEX = SingleArgRoute("books_index", Keys.COLLECTION_ID)
    val READER = MultiArgRoute("reader", listOf(Keys.COLLECTION_ID, Keys.BOOK_ID, Keys.HADITH_NUMBER))
    val NARRATOR_CHAIN = SingleArgRoute("narrators_chain", Keys.HADITH_URN)
    val SCHOLAR_INFO = SingleArgRoute("scholar_info", Keys.SCHOLAR_ID)
}