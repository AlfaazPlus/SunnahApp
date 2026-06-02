package com.alfaazplus.sunnah.ui.search

enum class SearchMatchingStrategy(val value: String) {
    ANY_WORD("any"),
    ALL_WORDS("all"),
    EXACT_PHRASE("exact");

    companion object {
        fun fromValue(value: String?): SearchMatchingStrategy {
            return entries.find { it.value == value } ?: ANY_WORD
        }
    }
}

data class SearchFilters(
    val selectedCollections: Set<String>? = null,
    val matchingStrategy: SearchMatchingStrategy = SearchMatchingStrategy.ANY_WORD,
) {
    val isEmpty: Boolean
        get() = selectedCollections.isNullOrEmpty() && matchingStrategy == SearchMatchingStrategy.ANY_WORD

    val isValid: Boolean
        get() = selectedCollections == null || selectedCollections.isNotEmpty()
}
