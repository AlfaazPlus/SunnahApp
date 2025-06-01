package com.alfaazplus.sunnah.ui.models.userdata

data class AddToCollectionRequest(
    val hadithCollectionId: Int,
    val hadithBookId: Int,
    val hadithNumber: String,
)
