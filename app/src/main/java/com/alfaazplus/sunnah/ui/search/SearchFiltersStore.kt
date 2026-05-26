package com.alfaazplus.sunnah.ui.search

import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.utils.shared_preference.PrefKey

object SearchFiltersStore {
    private val KEY_SLUGS = PrefKey(stringPreferencesKey("search_filter_collections"), "")

    fun read(): SearchFilters {
        val collectionsCsv = DataStoreManager.read(KEY_SLUGS)

        val collectionIds = collectionsCsv
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
            .takeIf { it.isNotEmpty() }

        return SearchFilters(
            selectedCollections = collectionIds,
        )
    }

    suspend fun write(filters: SearchFilters) {
        DataStoreManager.edit {
            this[KEY_SLUGS.key] = filters.selectedCollections
                ?.joinToString(",")
                ?: ""
        }
    }
}
