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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString
import com.alfaazplus.sunnah.ui.viewModels.HotdViewModel

@Composable
private fun HotdTitle() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF304F32),
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
    hotd: HadithOfTheDay,
) {
    val navController = LocalNavHostController.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3D3D3D),
            contentColor = Color.White,
        ),
        shape = MaterialTheme.shapes.small,
        onClick = {
            navController.navigate(
                Routes.READER.args(
                    hotd.hadith.collectionId,
                    hotd.hadith.bookId,
                    hotd.hadith.hadithNumber,
                )
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

@Composable
private fun Texts(hotd: HadithOfTheDay) {
    val hadithTextOption = ReaderUtils.getHadithTextOption()
    val isSerifFontStyle = ReaderUtils.getIsSerifFontStyle()

    val showArabic = hadithTextOption != ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION
    val showTranslation = hadithTextOption != ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (showArabic) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = hotd.hadith.hadithText
                    .parseAsHtml()
                    .toAnnotatedString(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = fontUthmani,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight * 1.5f,
            )
        }

        if (showTranslation) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = hotd.translation.hadithText
                    .parseAsHtml()
                    .toAnnotatedString(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = if (isSerifFontStyle) FontFamily.Serif else FontFamily.SansSerif,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Footer(hotd: HadithOfTheDay) {
    HorizontalDivider(
        color = Color(0xFF3D3D3D),
        thickness = 1.dp,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "${hotd.collectionName}: ${hotd.hadith.hadithNumber}",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
        )

        IconButton(onClick = { // todo:
        }) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = null,
                tint = Color.White,
            )
        }
        IconButton(onClick = { // todo:
        }) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_bookmark_plus),
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

@Composable
fun HadithOfTheDay(
    vm: HotdViewModel = hiltViewModel(),
) {
    val hotd0 by vm.hotdFlow.collectAsStateWithLifecycle()

    val gradientColors = listOf(
        Color(0xFF1F3620),
        Color(0xFF000000),
    )

    val hotd = hotd0 ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
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
                HotdTitle()
                ReadButton(hotd)
            }
            Texts(hotd)
            Footer(hotd)
        }
    }
}