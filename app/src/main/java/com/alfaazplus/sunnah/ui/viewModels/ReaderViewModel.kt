package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.helpers.HadithTextHelper
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import com.alfaazplus.sunnah.ui.models.ParsedHadith
import com.alfaazplus.sunnah.ui.utils.text.toHadithAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    var primaryColor by mutableStateOf(Color(0xFF000000))
    var onPrimaryColor by mutableStateOf(Color(0xFF000000))
    var initialized by mutableStateOf(false)

    var collectionId by mutableIntStateOf(0)
    val bookId = MutableLiveData(0)
    var hadithList by mutableStateOf(listOf<HadithWithTranslation>())
    var parsedHadithList by mutableStateOf(listOf<ParsedHadith>())

    var books: MutableLiveData<List<BookWithInfo>> = MutableLiveData(listOf())
    var cwi by mutableStateOf<CollectionWithInfo?>(null)
    var bwi by mutableStateOf<BookWithInfo?>(null)

    var currentHadithNumber by mutableStateOf("")

    init {
        bookId.observeForever {
            if (it != null) {
                viewModelScope.launch(Dispatchers.Main) {
                    loadHadiths()
                }
            }
        }

        books.observeForever { books ->
            if (books.isNotEmpty()) {
                bwi = books.firstOrNull { it.book.id == bookId.value }
            }
        }
    }

    suspend fun loadEssentials() {
        cwi = repo.getCollection(collectionId)
        books.value = repo.getBookList(collectionId)
    }

    private fun parseHadiths() {
        parsedHadithList = hadithList.map {
            val parsedHadith = ParsedHadith(it)

            if (!it.hadith.narratorPrefix.isNullOrEmpty()) {
                parsedHadith.narratorPrefixText = HadithTextHelper.prepareText(it.hadith.narratorPrefix)
            }

            parsedHadith.hadithText = HadithTextHelper.prepareText(it.hadith.hadithText).toHadithAnnotatedString(primaryColor, onPrimaryColor)

            if (!it.hadith.narratorSuffix.isNullOrEmpty()) {
                parsedHadith.narratorSuffixText = HadithTextHelper.prepareText(it.hadith.narratorSuffix)
            }

            if (it.translation != null) {
                parsedHadith.translationText =
                    HadithTextHelper.prepareText(it.translation.hadithText).toHadithAnnotatedString(primaryColor, onPrimaryColor)

                if (parsedHadith.translation?.grades?.contains("Sahih") == true) {
                    parsedHadith.gradeType = "sahih"
                } else if (parsedHadith.translation?.grades?.contains("Da'if") == true) {
                    parsedHadith.gradeType = "daif"
                } else if (parsedHadith.translation?.grades?.contains("Hasan") == true) {
                    parsedHadith.gradeType = "hasan"
                }
            }

            return@map parsedHadith
        }
    }

    private suspend fun loadHadiths() {
        hadithList = repo.getHadithList(collectionId, bookId.value!!)
        Logger.d("Hadith list size: ${hadithList.size}")
        parseHadiths()
        initialized = true
    }
}