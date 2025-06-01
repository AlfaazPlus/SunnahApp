package com.alfaazplus.sunnah.ui.models.userdata

data class AddToBookmarkRequest(
    val hadithCollectionId: Int,
    val hadithBookId: Int,
    val hadithNumber: String,
    val editMode: Boolean = false,
)
