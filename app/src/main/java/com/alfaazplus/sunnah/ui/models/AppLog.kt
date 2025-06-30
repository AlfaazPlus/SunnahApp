package com.alfaazplus.sunnah.ui.models

import java.io.File

data class AppLogModel(
    val datetime: String,
    val location: String,
    val file: File,
    val log: String,
    val logShort: String,
)