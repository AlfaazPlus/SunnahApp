package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.db.entities.scholars.Scholar
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NarratorsViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    private val _hadithId = MutableStateFlow<String?>(null)

    val narrators: StateFlow<List<Scholar>> = _hadithId
        .flatMapLatest { hadithId ->
            if (hadithId != null) {
                flow {
                    emit(repo.getNarratorsOfHadith(hadithId))
                }
            } else {
                flow { emit(emptyList()) }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    fun setHadithId(hadithId: String) {
        _hadithId.value = hadithId
    }
}
