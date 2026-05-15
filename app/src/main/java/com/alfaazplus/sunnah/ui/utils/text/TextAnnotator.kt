package com.alfaazplus.sunnah.ui.utils.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.alfaazplus.sunnah.ui.components.reader.HadithActions

fun buildHadithAnnotatedString(
    parts: List<HadithRichTextPart>,
    linkColor: Color,
    actions: HadithActions,
): AnnotatedString {
    val linkStyles = TextLinkStyles(
        style = SpanStyle(
            color = linkColor,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline,
        ),
        pressedStyle = SpanStyle(
            color = linkColor.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
        ),
    )

    return buildAnnotatedString {
        parts.forEach { part ->
            when (part) {
                is HadithRichTextPart.Plain -> {
                    withStyle(
                        SpanStyle(
                            fontWeight = if (part.bold) FontWeight.Bold else null,
                            fontStyle = if (part.italic) FontStyle.Italic else null,
                            textDecoration = if (part.underline) TextDecoration.Underline else TextDecoration.None,
                        )
                    ) {
                        append(part.text)
                    }
                }

                is HadithRichTextPart.HadithRef -> {
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "ref:${part.id}",
                            styles = linkStyles,
                            linkInteractionListener = {
                                actions.onQuickReferenceRequest(part.id)
                            },
                        )
                    ) {
                        append(part.text)
                    }
                }

                is HadithRichTextPart.QuranRef -> {
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "qref:${part.chapter}:${part.verses}",
                            styles = linkStyles,
                            linkInteractionListener = {
                                actions.onQuranReferenceRequest(part.chapter, part.verses)
                            },
                        )
                    ) {
                        append(part.text)
                    }
                }
            }
        }
    }
}
