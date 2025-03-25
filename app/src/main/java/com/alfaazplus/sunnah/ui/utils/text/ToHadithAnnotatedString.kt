package com.alfaazplus.sunnah.ui.utils.text

import android.text.Spanned
import android.text.style.URLSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.getSpans
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.ui.theme.alpha

fun CharSequence.toHadithAnnotatedString(primaryColor: Color, onPrimary: Color): AnnotatedString {
    return if (this is Spanned) {
        this.toHadithAnnotatedString(primaryColor, onPrimary)
    } else {
        buildAnnotatedString {
            append(this@toHadithAnnotatedString.toString())
        }
    }
}

fun Spanned.toHadithAnnotatedString(primaryColor: Color, onPrimary: Color): AnnotatedString {
    return buildAnnotatedString {
        append(this@toHadithAnnotatedString.toString())
        val urlSpans = getSpans<URLSpan>()

        urlSpans.forEach { urlSpan ->
            val start = getSpanStart(urlSpan)
            val end = getSpanEnd(urlSpan)
            addStyle(
                SpanStyle(
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                ), start, end
            )
            addStringAnnotation("ref", urlSpan.url, start, end)
        }
    }
}