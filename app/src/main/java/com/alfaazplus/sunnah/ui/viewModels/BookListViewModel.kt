package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    var cwt by mutableStateOf<CollectionWithTranslation?>(null)
    var books by mutableStateOf<List<BookWithTranslation>>(emptyList())

    suspend fun setCollectionId(collectionId: String) {
        if (cwt?.collection?.id == collectionId) return

        val langCode = ReaderPreferences.getHadithTranslation()
        cwt = repo.getCollectionById(collectionId, langCode)
        books = repo.loadBooks(collectionId, langCode)
    }
}
