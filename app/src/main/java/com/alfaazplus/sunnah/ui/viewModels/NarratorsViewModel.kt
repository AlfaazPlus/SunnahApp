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
class NarratorsViewModel @Inject constructor(
    private val repo: HadithRepositoryImpl,
) : ViewModel() {
    private var _urn = MutableStateFlow<Int?>(null)

    val narrators: StateFlow<List<Scholar>> = _urn
        .flatMapLatest { urn ->
            if (urn != null) {
                flow {
                    emit(repo.getNarratorsOfHadith(urn))
                }
            } else {
                flow {
                    val empty = listOf<Scholar>()
                    emit(empty)
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf(),
        )


    fun setUrn(urn: Int) {
        _urn.value = urn
    }
}