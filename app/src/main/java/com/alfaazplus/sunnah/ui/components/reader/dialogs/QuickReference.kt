package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.navOptions
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.reader.HadithItemView
import com.alfaazplus.sunnah.ui.components.reader.LocalHadithActions
import com.alfaazplus.sunnah.ui.components.reader.LocalReader
import com.alfaazplus.sunnah.ui.components.reader.ReaderProvider
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.extension.bottomBorder
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.reader.ReaderChangeManager
import com.alfaazplus.sunnah.ui.utils.reader.ReaderItemsBuilder
import com.alfaazplus.sunnah.ui.utils.text.ComposeUiConfig
import com.alfaazplus.sunnah.ui.utils.text.TextBuilderParams
import com.alfaazplus.sunnah.ui.viewModels.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

data class QuickReferenceData(
    val title: String? = null,
    val hadithIds: List<String>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickReference(
    data: QuickReferenceData?,
    onClose: () -> Unit,
) {
    if (data == null || data.hadithIds.isEmpty()) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ReaderProvider {
        val reader = LocalReader.current

        ModalBottomSheet(
            onDismissRequest = onClose,
            sheetState = sheetState,
            scrimColor = colorScheme.scrim.alpha(0.5f),
            containerColor = reader.containerColor,
            contentColor = reader.contentColor,
            dragHandle = null,
            contentWindowInsets = { WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom) },
        ) {
            QuickReferenceContent(
                data = data,
                onClose = onClose,
            )
        }
    }
}

@Composable
private fun QuickReferenceContent(
    data: QuickReferenceData,
    onClose: () -> Unit,
    appVm: AppViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val navController = LocalNavHostController.current
    val hadithActions = LocalHadithActions.current

    val colors by rememberUpdatedState(colorScheme)

    var items by remember { mutableStateOf<List<ReaderLayoutItem.HadithUI>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(data.hadithIds, hadithActions, colors) {
        ReaderChangeManager
            .changeFlow()
            .collectLatest { config ->
                isLoading = true

                withContext(Dispatchers.IO) {
                    val params = TextBuilderParams(
                        uiConfig = ComposeUiConfig(
                            context = context,
                            colors = colors,
                        ),
                        hadithActions = hadithActions,
                        translationId = config.selectedTranslationLangCode,
                        arabicSizePercent = config.txtSizePercentArabic,
                        translationSizePercent = config.txtSizePercentTranslation,
                        hadithTextOption = config.hadithTextOption,
                        isSanadEnabled = config.isSanadEnabled,
                        isSerifFontStyle = config.isSerifFontStyle,
                    )

                    items = ReaderItemsBuilder.buildQuickReferenceItems(
                        repo = appVm.repo,
                        hadithIds = data.hadithIds,
                        params = params,
                    )
                }

                isLoading = false
            }
    }

    val resolvedTitle = when {
        !data.title.isNullOrEmpty() -> AnnotatedString(data.title)
        items.size == 1 -> items.first().visibleNumbering
        items.size > 1 -> AnnotatedString(stringResource(R.string.quick_reference_n_hadiths, items.size))
        else -> AnnotatedString("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
    ) {
        QuickReferenceHeader(
            title = resolvedTitle,
            showOpen = items.isNotEmpty() && !isLoading,
            onOpen = {
                val first = items.firstOrNull() ?: return@QuickReferenceHeader

                navController.navigate(
                    Routes.READER.args(first.bookId, first.hadithId),
                    navOptions = navOptions {
                        launchSingleTop = true
                    },
                )

                onClose()
            },
            onClose = onClose,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Loader()
                }
            }

            items.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.quick_reference_not_found),
                        style = typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    items(
                        items = items,
                        key = { item -> item.key },
                    ) { hadithUi ->
                        HadithItemView(
                            hadithUi = hadithUi,
                            isVertical = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickReferenceHeader(
    title: AnnotatedString,
    showOpen: Boolean,
    onOpen: () -> Unit,
    onClose: () -> Unit,
) {
    val iconTint = colorScheme.onSurface.alpha(0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bottomBorder()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(R.drawable.ic_x),
                contentDescription = stringResource(R.string.close),
                tint = colorScheme.onSurface,
            )
        }

        Text(
            text = title,
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.primary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        if (showOpen) {
            IconButton(onClick = onOpen) {
                Icon(
                    painter = painterResource(R.drawable.ic_open_external),
                    contentDescription = stringResource(R.string.open_in_reader),
                    tint = iconTint,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}
