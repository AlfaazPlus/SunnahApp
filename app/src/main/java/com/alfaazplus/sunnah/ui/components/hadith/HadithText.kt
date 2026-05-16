package com.alfaazplus.sunnah.ui.components.hadith

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString


@Composable
fun HadithText(
    text: AnnotatedString?,
    modifier: Modifier = Modifier,
) {
    if (text == null) return

    SelectionContainer {
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
        )
    }
}
