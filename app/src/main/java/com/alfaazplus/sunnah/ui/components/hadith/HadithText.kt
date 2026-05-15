package com.alfaazplus.sunnah.ui.components.hadith

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.ui.components.reader.LocalHadithActions
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.models.QuranReference

fun handleOpenQuranReference(context: Context, reference: String, quranAppNotInstallCallback: () -> Unit) {
    val parts = reference.split(":")
    val chapterNo = parts[0].toInt()
    val versesStr = parts[1]

    val verses = versesStr.split("-")
    val fromVerse = verses[0].toInt()
    var toVerse = fromVerse

    if (verses.size > 1) {
        toVerse = verses[1].toInt()
    }

    try {
        NavigationHelper.openQuranReference(context, QuranReference(chapterNo, fromVerse, toVerse))
    } catch (e: Exception) {
        Logger.e(e)
        quranAppNotInstallCallback()
    }
}

@Composable
fun HadithText(
    text: AnnotatedString?,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    if (text == null) return

    val context = LocalContext.current
    val actions = LocalHadithActions.current

    val clickable = remember(text) {
        text
            .getStringAnnotations(0, text.length - 1)
            .any { it.tag == "ref" || it.tag == "qref" }
    }

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    SelectionContainer {
        Text(
            text = text,
            modifier = modifier
                .fillMaxWidth()
                .then(if (clickable) Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { pos ->
                        layoutResult.value?.let { layoutResult ->
                            val position = layoutResult.getOffsetForPosition(pos)

                            text
                                .getStringAnnotations(position, position)
                                .firstOrNull()
                                ?.let { sa ->
                                    if (sa.tag == "ref") {
                                        actions.onQuickReferenceRequest(sa.item)
                                    } else if (sa.tag == "qref") {
                                        actions.onQuranReferenceRequest(sa.item)
                                    }
                                }
                        }
                    })
                } else Modifier),
            fontFamily = fontFamily,
            fontSize = fontSize,
            lineHeight = lineHeight,
            onTextLayout = {
                layoutResult.value = it
            },
        )
    }
}
