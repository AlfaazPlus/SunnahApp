package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.repository.hadith.HadithRepository2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val repo: HadithRepository2,
) : ViewModel() 
