package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NarratorsViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    var narrators by mutableStateOf<List<Scholar>>(listOf())

    suspend fun loadNarrators(urn: Int) {
        narrators = repo.getNarratorsOfHadith(urn)
    }
}