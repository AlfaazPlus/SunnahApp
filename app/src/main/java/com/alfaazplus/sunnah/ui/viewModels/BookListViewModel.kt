package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.repository.hadith.HadithRepositoryImpl
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repo: HadithRepositoryImpl,
) : ViewModel() {
    var collectionId by mutableIntStateOf(0)
    var collectionWithInfo by mutableStateOf<CollectionWithInfo?>(null)
    var books by mutableStateOf(listOf<BookWithInfo>())

    suspend fun setCollectionId(collectionId: Int) {
        if (this.collectionId == collectionId) return

        this.collectionId = collectionId
        collectionWithInfo = repo.getCollection(collectionId)
        books = repo.getBookList(collectionId)
    }
}
