package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.components.reader.HadithActions
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.reader.ReaderChangeManager
import com.alfaazplus.sunnah.ui.utils.reader.ReaderItemsBuilder
import com.alfaazplus.sunnah.ui.utils.text.ComposeUiConfig
import com.alfaazplus.sunnah.ui.utils.text.TextBuilderParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HotdViewModel @Inject constructor(
    val repo: HadithRepository,
) : ViewModel() {
    val hotdFlow: StateFlow<HadithOfTheDay?> = flow {
        emit(HadithHelper.getHadithOfTheDay(repo))
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    private val _hadithUi = MutableStateFlow<ReaderLayoutItem.HadithUI?>(null)
    val hadithUi: StateFlow<ReaderLayoutItem.HadithUI?> = _hadithUi.asStateFlow()

    suspend fun observeHadithUi(
        uiConfig: ComposeUiConfig,
        hadithActions: HadithActions,
    ) {
        combine(
            hotdFlow.filterNotNull(),
            ReaderChangeManager.changeFlow(),
        ) { hotd, config ->
            hotd.hwc.hadithId to config
        }.collectLatest { (hadithId, config) ->
            _hadithUi.value = withContext(Dispatchers.IO) {
                val params = TextBuilderParams(
                    uiConfig = uiConfig,
                    translationId = config.selectedTranslationLangCode,
                    hadithActions = hadithActions,
                    arabicSizePercent = config.txtSizePercentArabic,
                    translationSizePercent = config.txtSizePercentTranslation,
                    hadithTextOption = if (config.hadithTextOption == HadithTextOption.ONLY_ARABIC) config.hadithTextOption else HadithTextOption.ONLY_TRANSLATION,
                    isSanadEnabled = false,
                    isSerifFontStyle = config.isSerifFontStyle,
                )

                ReaderItemsBuilder
                    .buildQuickReferenceItems(
                        repo = repo,
                        hadithIds = listOf(hadithId),
                        params = params,
                    )
                    .firstOrNull()
            }
        }
    }
}
