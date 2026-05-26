package com.alfaazplus.sunnah.ui.search

data class SearchFilters(
    val selectedCollections: Set<String>? = null,
) {
    val isEmpty: Boolean
        get() = selectedCollections.isNullOrEmpty()

    val isValid: Boolean
        get() = selectedCollections == null || selectedCollections.isNotEmpty()
}
