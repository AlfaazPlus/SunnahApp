package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.repository.hadith.HadithRepositoryImpl
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
class ScholarInfoViewModel @Inject constructor(
    private val repo: HadithRepositoryImpl,
) : ViewModel() {
    private var _scholarId = MutableStateFlow<Int?>(null)

    val scholar: StateFlow<Scholar?> = _scholarId
        .flatMapLatest { scholarId ->
            if (scholarId != null) {
                flow {
                    emit(repo.getScholarInfo(scholarId))
                }
            } else {
                flow { emit(null) }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )


    fun setScholarId(scholarId: Int) {
        _scholarId.value = scholarId
    }
}
