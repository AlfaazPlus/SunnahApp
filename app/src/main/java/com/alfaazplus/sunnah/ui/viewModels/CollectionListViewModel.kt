package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.helpers.HadithHelper.getIncludedCollections
import com.alfaazplus.sunnah.repository.hadith.HadithRepositoryImpl
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CollectionListViewModel @Inject constructor(
    private val repo: HadithRepositoryImpl,
) : ViewModel() {

    private val _collections = MutableStateFlow<List<CollectionWithInfo>>(emptyList())
    var collections: StateFlow<List<CollectionWithInfo>> = _collections

    suspend fun loadCollections() {
        val collections = getIncludedCollections()
        collections.forEach {
            it.isDownloaded = isCollectionDownloaded(it.collection.id)
        }

        _collections.value = collections
    }

    private suspend fun isCollectionDownloaded(collectionId: Int): Boolean {
        return try {
            repo.getCollection(collectionId)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteCollection(collectionId: Int, onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCollection(collectionId)
            loadCollections()

            withContext(Dispatchers.Main) {
                onDeleted()
            }
        }
    }
}
