package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HadithRepoViewModel @Inject constructor(
    private val repository: HadithRepository,
) : ViewModel() {
    val repo get() = repository
}
