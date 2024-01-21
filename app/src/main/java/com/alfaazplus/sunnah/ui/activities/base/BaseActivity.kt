package com.alfaazplus.sunnah.ui.activities.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.alfaazplus.sunnah.ui.viewModels.AppPreferenceViewModel

abstract class BaseActivity : ComponentActivity() {
    lateinit var appPreferenceModel: AppPreferenceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelProvider = ViewModelProvider(this)
        appPreferenceModel = viewModelProvider.get()
    }
}