package com.alfaazplus.sunnah.ui.models.userdata

data class AddToBookmarkRequest(
    val hadithId: String,
    val editMode: Boolean = false,
    val openInReader: Boolean = true,
)
