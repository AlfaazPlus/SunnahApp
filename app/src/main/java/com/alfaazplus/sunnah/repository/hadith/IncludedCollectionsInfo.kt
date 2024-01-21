@file:Suppress("SpellCheckingInspection")

package com.alfaazplus.sunnah.repository.hadith

private val BUKHARI = Pair(
    "[1, \"collection\", 1, 1, 1, \"صحيح البخاري\", null, null, null]",
    "[1, 1, \"Sahih al-Bukhari\", \"Sahih al-Bukhari is a collection of hadith compiled by Imam Muhammad al-Bukhari (d. 256 AH/870 AD) (rahimahullah).\\\\r\\\\n\\\\r\\\\nHis collection is recognized by the overwhelming majority of the Muslim world to be the most authentic collection of reports of the <i>Sunnah</i> of the Prophet Muhammad (ﷺ). It contains over 7500 hadith (with repetitions) in 97 books.\\\\r\\\\n\\\\r\\\\nThe translation provided here is by Dr. M. Muhsin Khan.\", null, \"The numbering below corresponds with Shaykh Muhammad Fuad `Abd al-Baqi's (rahimahullah) numbering scheme.\", \"en\"]"
)
private val MUSLIM = Pair(
    "[2, \"collection\", 0, 1, 1, \"صحيح مسلم\", null, null, null]",
    "[2, 2, \"Sahih Muslim\", \"Sahih Muslim is a collection of hadith compiled by Imam Muslim ibn al-Hajjaj al-Naysaburi (rahimahullah).\\\\r\\\\nHis collection is considered to be one of the most authentic\\\\r\\\\ncollections of the Sunnah of the Prophet (ﷺ), and along with\\\\r\\\\nSahih al-Bukhari forms the \\\\\\\"Sahihain,\\\\\\\" or the \\\\\\\"Two Sahihs.\\\\\\\"\\\\r\\\\nIt contains roughly 7500 hadith (with repetitions) in 57 books.\\\\r\\\\n<br>\\\\r\\\\nThe translation provided here is by Abdul Hamid Siddiqui.\", null, \"The numbering below corresponds with Shaykh Muhammad Fuad `Abd al-Baqi's (rahimahullah) numbering scheme.\", \"en\"]"
)
private val ABU_DAWUD = Pair(
    "[3, \"collection\", 1, 1, 1, \"سنن النسائي\", null, null, null]",
    "[3, 3, \"Sunan an-Nasa'i\", \"Sunan an-Nasa'i is a collection of hadith compiled by Imam Ahmad an-Nasa'i (rahimahullah).\\\\r\\\\nHis collection is unanimously considered to be one of the six canonical collections of hadith (Kutub as-Sittah)\\\\r\\\\nof the Sunnah of the Prophet (ﷺ).\\\\r\\\\nIt contains roughly 5700 hadith (with repetitions) in 52 books.\\\\r\\\\n\", null, \"The numbering below corresponds with Shaykh `Abd al-Fattah Abu Ghuddah's (rahimahullah) numbering scheme.\", \"en\"]"
)
private val NASAI = Pair(
    "[4, \"collection\", 0, 1, 1, \"سنن أبي داود\", null, null, null]",
    "[4, 4, \"Sunan Abi Dawud\", \"Sunan Abi Dawud is a collection of hadith compiled by Imam Abu Dawud Sulayman ibn al-Ash'ath as-Sijistani (rahimahullah). It is widely considered to be among the six canonical collections of hadith (Kutub as-Sittah) of the Sunnah of the Prophet (ﷺ). It consists of 5274 ahadith in 43 books.\", null, \"The numbering below corresponds with Shaykh Muhammad Muhi ad-Din `Abd al-Hameed's (rahimahullah) numbering scheme.\", \"en\"]"
)
private val TIRMIDHI = Pair(
    "[5, \"collection\", 1, 1, 1, \"جامع الترمذي\", null, null, null]",
    "[5, 5, \"Jami` at-Tirmidhi\", \"Jami` at-Tirmidhi is a collection of hadith compiled by Imam Abu `Isa Muhammad at-Tirmidhi (rahimahullah). His collection is unanimously considered to be one of the six canonical collections of hadith (Kutub as-Sittah) of the Sunnah of the Prophet (ﷺ). It contains roughly 4400 hadith (with repetitions) in 46 books.\", null, \"The numbering below corresponds with the numbering scheme started by Shaykh Ahmad Shakir, then continued by Shaykh Muhammad Fu'ad 'Abdul Baaqi, and finished by Shaykh Ibrahim 'Atwah 'Aood (rahimahumullah).\", \"en\"]"
)
private val IBN_MAJAH = Pair(
    "[6, \"collection\", 1, 1, 1, \"سنن ابن ماجه\", null, null, null]",
    "[6, 6, \"Sunan Ibn Majah\", \"Sunan Ibn Majah is a collection of hadith compiled by Imam Muhammad bin Yazid Ibn Majah al-Qazvini (rahimahullah). It is widely considered to be the sixth of the six canonical collection of hadith (Kutub as-Sittah) of the Sunnah of the Prophet (ﷺ). It consists of 4341 ahadith in 37 books.\", null, \"The numbering below corresponds with Shaykh Muhammad Fuad `Abd al-Baqi's (rahimahullah) numbering scheme.\", \"en\"]"
)
val HADITH_COLLECTIONS = listOf(BUKHARI, MUSLIM, ABU_DAWUD, NASAI, TIRMIDHI, IBN_MAJAH)