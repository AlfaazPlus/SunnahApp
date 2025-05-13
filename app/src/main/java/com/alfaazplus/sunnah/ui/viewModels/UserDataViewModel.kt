package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    val repo get() = repository

    val userCollections: StateFlow<List<UserCollection>> = repository
        .loadAllUserCollections()
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )
}
