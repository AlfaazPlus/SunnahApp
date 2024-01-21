package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.helpers.HadithHelper.getIncludedCollections
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionListViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    var collections: List<CollectionWithInfo> by mutableStateOf(listOf())

    suspend fun loadCollections() {
        if (collections.isNotEmpty()) return

        val collections = getIncludedCollections()
        collections.forEach {
            it.isDownloaded = isCollectionDownloaded(it.collection.id)
        }

        this.collections = collections
    }

    private suspend fun isCollectionDownloaded(collectionId: Int): Boolean {
        return try {
            repo.getCollection(collectionId)
            true
        } catch (e: Exception) {
            false
        }
    }
}
