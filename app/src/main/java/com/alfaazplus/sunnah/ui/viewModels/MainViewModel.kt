package com.alfaazplus.sunnah.ui.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.db.databases.AppDatabase
import com.alfaazplus.sunnah.helpers.DatabaseHelper
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPHadithConfigs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val database: AppDatabase,
) : ViewModel() {
    var loading by mutableStateOf(!SPHadithConfigs.getAssetHadithsImported())

    suspend fun initializeHadiths(context: Context) {
        if (SPHadithConfigs.getAssetHadithsImported()) {
            loading = false
            return
        }

        DatabaseHelper.populateHadithDataFromAssets(database, context)

        loading = false
    }
}
