package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.stringPreferencesKey
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
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.ReaderUtils.HADITH_LAYOUT_HORIZONTAL
import com.alfaazplus.sunnah.ui.utils.datatype.ReadOnce
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
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

    var hadithLayout by mutableStateOf(ReaderUtils.HADITH_LAYOUT_HORIZONTAL)

    var collectionId by mutableIntStateOf(0)
    val bookId = MutableLiveData(0)
    var hadithList by mutableStateOf(listOf<HadithWithTranslation>())
    var parsedHadithList by mutableStateOf(listOf<ParsedHadith>())

    var books: MutableLiveData<List<BookWithInfo>> = MutableLiveData(listOf())
    var cwi by mutableStateOf<CollectionWithInfo?>(null)
    var bwi by mutableStateOf<BookWithInfo?>(null)

    /**
     * Hadith number, consumed
     */
    var initialHadithNumber by mutableStateOf(Pair<String?, Boolean>(null, false))
    var currentHadithNumber by mutableStateOf("")

    /**
     * Hadith number, consumed
     */
    val transientScroll = ReadOnce<String?>()

    init {
        bookId.observeForever {
            if (it != null) {
                viewModelScope.launch(Dispatchers.Main) {
                    loadHadiths()
                    setCurrentBookInfo(books.value)
                }
            }
        }

        books.observeForever { books ->
            setCurrentBookInfo(books)
        }
    }

    private fun setCurrentBookInfo(books: List<BookWithInfo>?) {
        bwi = books?.firstOrNull { it.book.id == bookId.value }
    }

    suspend fun loadEssentials() {
        hadithLayout = DataStoreManager.read(stringPreferencesKey(Keys.HADITH_LAYOUT), HADITH_LAYOUT_HORIZONTAL)
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