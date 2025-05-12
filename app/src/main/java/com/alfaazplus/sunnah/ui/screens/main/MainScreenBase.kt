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
    data object Home : MainScreenBase(Routes.HOME, R.string.home, R.drawable.ic_home)
    data object Library : MainScreenBase(Routes.LIBRARY, R.string.library, R.drawable.ic_library)
    data object Search : MainScreenBase(Routes.SEARCH, R.string.search, R.drawable.ic_search)
}