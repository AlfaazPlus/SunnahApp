package com.alfaazplus.sunnah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.IconButton
import com.alfaazplus.sunnah.ui.components.reader.LocalHadithActions
import com.alfaazplus.sunnah.ui.components.reader.ReaderProvider
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.theme.tightTextStyle
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.text.ComposeUiConfig
import com.alfaazplus.sunnah.ui.viewModels.HotdViewModel

private data class HotdPalette(
    val gradient: List<Color>,
    val titleContainer: Color,
    val mutedSurface: Color,
    val link: Color,
)

@Composable
private fun rememberHotdPalette(): HotdPalette {
    val scheme = MaterialTheme.colorScheme
    val primary = scheme.primary
    val base = if (scheme.primaryContainer.luminance() < 0.45f) {
        lerp(scheme.primaryContainer, primary, 0.45f)
    } else {
        primary
    }

    return remember(primary, base) {
        HotdPalette(
            gradient = listOf(
                lerp(base, Color.Black, 0.48f),
                lerp(base, Color.Black, 0.62f),
            ),
            titleContainer = lerp(base, Color.Black, 0.32f),
            mutedSurface = lerp(base, Color.Black, 0.42f),
            link = primary,
        )
    }
}

private fun ColorScheme.forHotdCard(linkColor: Color) = copy(
    primary = linkColor,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun HadithOfTheDay(
    vm: HotdViewModel = hiltViewModel(),
) {
    ReaderProvider {
        HotdCard(vm = vm)
    }
}

@Composable
private fun HotdCard(
    vm: HotdViewModel,
) {
    val context = LocalContext.current
    val hadithActions = LocalHadithActions.current
    val colors by rememberUpdatedState(MaterialTheme.colorScheme)
    val palette = rememberHotdPalette()

    LaunchedEffect(hadithActions, colors, palette.link) {
        val hotdColors = colors.forHotdCard(palette.link)
        val uiConfig = ComposeUiConfig(
            context = context,
            colors = hotdColors,
        )

        vm.observeHadithUi(
            uiConfig = uiConfig,
            hadithActions = hadithActions,
        )
    }

    val hUi by vm.hadithUi.collectAsStateWithLifecycle()
    val hadithUi = hUi ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = palette.gradient,
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f),
                ),
                shape = MaterialTheme.shapes.medium,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HotdTitle(palette)
                ReadButton(hadithUi, palette)
            }

            HotdTexts(hadithUi)
            HotdFooter(
                hadithUi = hadithUi,
                palette = palette,
                repo = vm.repo,
            )
        }
    }
}

@Composable
private fun HotdTexts(hadithUi: ReaderLayoutItem.HadithUI) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        hadithUi.parsedArabicText?.let { arabicText ->
            HotdAnnotatedText(
                text = arabicText,
            )
        }

        hadithUi.parsedTranslationText?.let { translationText ->
            HotdAnnotatedText(
                text = translationText,
            )
        }
    }
}

@Composable
private fun HotdAnnotatedText(
    text: AnnotatedString,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        color = Color.White,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun HotdFooter(
    hadithUi: ReaderLayoutItem.HadithUI,
    palette: HotdPalette,
    repo: HadithRepository,
) {
    val context = LocalContext.current
    val actions = LocalHadithActions.current
    val translationLangCode = ReaderPreferences.observeHadithTranslation()

    val hadithNumber = hadithUi.hwc.hadith.number.orEmpty()
    val collectionName by produceState("", hadithUi.collectionId, translationLangCode) {
        value = repo.getCollectionName(hadithUi.collectionId, translationLangCode)
    }

    val iconTint = Color.LightGray

    HorizontalDivider(
        color = palette.mutedSurface,
        thickness = 1.dp,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "${collectionName}: $hadithNumber",
            style = MaterialTheme.typography.labelMedium.merge(tightTextStyle),
            color = iconTint,
        )

        if (hadithUi.hasNarratorsChain) {
            IconButton(
                painter = painterResource(id = R.drawable.ic_users),
                contentDescription = stringResource(R.string.desc_narrators_chain),
                tint = iconTint,
                small = true,
            ) {
                actions.showNarratorsChain(hadithUi.hadithId)
            }
        }

        IconButton(
            painter = painterResource(id = R.drawable.ic_share),
            tint = iconTint,
            small = true,
        ) {
            HadithHelper.shareHadith(
                context,
                hwc = hadithUi.hwc,
                collectionName = collectionName,
                bookName = null,
                translationLangCode = translationLangCode,
            )
        }
    }
}

@Composable
private fun HotdTitle(palette: HotdPalette) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = palette.titleContainer,
            contentColor = Color.White,
        ),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = null,
                tint = Color.White,
            )
            Text(
                text = stringResource(R.string.hadith_of_the_day),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun ReadButton(
    hadithUi: ReaderLayoutItem.HadithUI,
    palette: HotdPalette,
) {
    val navController = LocalNavHostController.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = palette.mutedSurface,
            contentColor = Color.White,
        ),
        shape = MaterialTheme.shapes.small,
        onClick = {
            navController.navigate(
                Routes.READER.args(hadithUi.bookId, hadithUi.hadithId)
            )
        },
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp,
            ),
        ) {
            Text(
                text = "Read",
                style = MaterialTheme.typography.labelMedium,
            )
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}
