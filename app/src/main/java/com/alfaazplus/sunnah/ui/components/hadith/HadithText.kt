package com.alfaazplus.sunnah.ui.components.hadith

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import com.alfaazplus.sunnah.Logger
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
    quranAppNotInstallCallback: () -> Unit
) {
    if (text == null) return

    val context = LocalContext.current
    val clickable = text.getStringAnnotations(0, text.length - 1).any { it.tag == "ref" }
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val quranRefs = remember(layoutResult, text) {
        text.getStringAnnotations("ref", 0, text.lastIndex)
    }

    Text(
        text = text,
        modifier = modifier.then(if (clickable) Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { pos ->
                    layoutResult.value?.let { layoutResult ->
                        val position = layoutResult.getOffsetForPosition(pos)
                        text
                            .getStringAnnotations(position, position)
                            .firstOrNull()
                            ?.let { sa ->
                                if (sa.tag == "ref") {
                                    handleOpenQuranReference(context, sa.item, quranAppNotInstallCallback)
                                }
                            }
                    }
                })
            }
            .semantics {
                if (quranRefs.size == 1) {
                    role = Role.Button
                    onClick("Link (${text.substring(quranRefs[0].start, quranRefs[0].end)}") {
                        handleOpenQuranReference(context, quranRefs[0].item, quranAppNotInstallCallback)
                        true
                    }
                } else {
                    customActions = quranRefs.map {
                        CustomAccessibilityAction("Link (${text.substring(it.start, it.end)})") {
                            handleOpenQuranReference(context, it.item, quranAppNotInstallCallback)
                            true
                        }
                    }
                }
            } else Modifier),
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = {
            layoutResult.value = it
        },
    )
}