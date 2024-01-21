package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.rememberPreference

@Composable
fun TextSizeSlider(key: String, title: Int, previewText: String) {
    val min = 50f
    val max = 200f
    val default = if (key == Keys.TEXT_SIZE_ARABIC) 110f else 100f
    val steps = max.toInt() - min.toInt()

    var textSize by rememberPreference(key, default)
    val multiplier = textSize / 100

    ListItemCategoryLabel(stringResource(title))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = Modifier
                .weight(1f),
            value = textSize,
            onValueChange = { textSize = it },
            valueRange = min..max,
            steps = steps
        )
        Text(
            text = "${textSize.toInt()}%",
            modifier = Modifier
                .padding(start = 10.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (key == Keys.TEXT_SIZE_ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Text(
                previewText,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize * multiplier,
                fontFamily = if (key == Keys.TEXT_SIZE_ARABIC) fontUthmani else MaterialTheme.typography.bodyLarge.fontFamily,
                lineHeight = if (key == Keys.TEXT_SIZE_ARABIC) 32.sp else MaterialTheme.typography.bodyLarge.lineHeight,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSizeSheet(sheetState: SheetState, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextSizeSlider(
                Keys.TEXT_SIZE_ARABIC,
                R.string.arabic_text_size,
                previewText = " مَنْ صَامَ رَمَضَانَ إِيمَانًا وَاحْتِسَابًا غُفِرَ لَهُ مَا تَقَدَّمَ مِنْ ذَنْبِهِ"
            )
            TextSizeSlider(
                Keys.TEXT_SIZE_TRANSLATION,
                R.string.translation_text_size,
                previewText = "Whoever fasts Ramadan out of faith and in the hope of reward, he will be forgiven his previous sins."
            )
        }
    }
}