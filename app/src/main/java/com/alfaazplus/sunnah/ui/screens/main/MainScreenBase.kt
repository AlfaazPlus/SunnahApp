package com.alfaazplus.sunnah.ui.screens.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.keys.Routes

sealed class MainScreenBase(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val icon: Int,
) {
    object Home : MainScreenBase(Routes.HOME, R.string.home, R.drawable.ic_home)
    object History : MainScreenBase(Routes.HISTORY, R.string.history, R.drawable.ic_history)
    object Search : MainScreenBase(Routes.SEARCH, R.string.search, R.drawable.ic_search)
}