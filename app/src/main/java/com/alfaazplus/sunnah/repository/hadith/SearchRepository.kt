package com.alfaazplus.sunnah.repository.hadith

import com.alfaazplus.sunnah.db.databases.SearchIndexDatabase

class SearchRepository(
    private val database: SearchIndexDatabase,
    private val hadithRepo: HadithRepository,
) {}
