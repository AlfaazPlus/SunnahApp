package com.alfaazplus.sunnah.ui.utils.text

import android.graphics.Typeface
import android.text.Editable
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import androidx.core.text.parseAsHtml
import org.xml.sax.Attributes
import java.util.Locale
import java.util.TreeSet

sealed interface HadithRichTextPart {
    data class Plain(
        val text: String,
        val bold: Boolean,
        val italic: Boolean,
        val underline: Boolean,
    ) : HadithRichTextPart

    data class HadithRef(
        val text: String,
        val id: String,
    ) : HadithRichTextPart

    data class QuranRef(
        val text: String,
        val chapter: Int,
        val verses: String,
    ) : HadithRichTextPart
}

private const val TAG_REF = "ref"
private const val TAG_REF_ATTR_ID = "id"
private const val TAG_QREF = "qref"
private const val TAG_QREF_ATTR_CHAPTER = "chapter"
private const val TAG_QREF_ATTR_VERSES = "verses"

private val LINE_BREAK_PATTERN = Regex("(?i)<br\\s*/?>")
private val TAG_PATTERN = Regex("<[^>]*>")

fun parseHadithText(html: String): List<HadithRichTextPart> {
    if (html.isBlank()) return emptyList()

    val normalized = html
        .replace("\u0000", "")
        .replace("\r\n", "\n")
        .replace("\r", "\n")
        .replace(LINE_BREAK_PATTERN, "<br/>")
        .replace("&nbsp;", " ")

    return try {
        val spanned = normalized.parseAsHtml(
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            null,
            HtmlParser(HadithBlockTagHandler()),
        )

        spanned.toHadithRichTextParts()
    } catch (_: Exception) {
        val fallback = normalized
            .replace(LINE_BREAK_PATTERN, "\n")
            .replace(TAG_PATTERN, "")
            .ifBlank { html.replace(TAG_PATTERN, "") }

        if (fallback.isBlank()) emptyList()
        else listOf(
            HadithRichTextPart.Plain(
                text = fallback,
                bold = false,
                italic = false,
                underline = false,
            )
        )
    }
}

private class RefSpan(val id: String)
private class QuranRefSpan(val chapter: String, val verses: String)

private class HadithBlockTagHandler : HtmlTagHandler {
    private var refStart: Int? = null
    private var refId: String? = null

    private var qrefStart: Int? = null
    private var qrefChapter: String? = null
    private var qrefVerses: String? = null

    override fun handleTag(
        opening: Boolean,
        tag: String?,
        output: Editable?,
        attributes: Attributes?,
    ): Boolean {
        if (output == null) return false

        return when (tag?.lowercase(Locale.US)) {
            TAG_REF -> handleHadithRef(opening, output, attributes)
            TAG_QREF -> handleQuranRef(opening, output, attributes)
            else -> false
        }
    }

    private fun handleHadithRef(opening: Boolean, output: Editable, attributes: Attributes?): Boolean {
        if (opening && attributes != null) {
            refStart = output.length
            refId = attributes
                .getValue(TAG_REF_ATTR_ID)
                .orEmpty()
        } else if (!opening && refStart != null && refId != null) {
            output.setSpan(
                RefSpan(refId!!),
                refStart!!,
                output.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )

            refStart = null
            refId = null
        }

        return true
    }

    private fun handleQuranRef(opening: Boolean, output: Editable, attributes: Attributes?): Boolean {
        if (opening && attributes != null) {
            qrefStart = output.length
            qrefChapter = attributes.getValue(TAG_QREF_ATTR_CHAPTER)
            qrefVerses = attributes.getValue(TAG_QREF_ATTR_VERSES) ?: ""
        } else if (!opening && qrefStart != null && qrefChapter != null && qrefVerses != null) {
            output.setSpan(
                QuranRefSpan(qrefChapter!!, qrefVerses!!),
                qrefStart!!,
                output.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )

            qrefStart = null
            qrefChapter = null
            qrefVerses = null
        }

        return true
    }
}

private fun Spanned.toHadithRichTextParts(): List<HadithRichTextPart> {
    val content = toString()
    if (content.isEmpty()) return emptyList()

    val boundaries = TreeSet<Int>()
    boundaries.add(0)
    boundaries.add(content.length)

    getSpans<StyleSpan>().forEach { span ->
        boundaries.add(getSpanStart(span))
        boundaries.add(getSpanEnd(span))
    }
    getSpans<UnderlineSpan>().forEach { span ->
        boundaries.add(getSpanStart(span))
        boundaries.add(getSpanEnd(span))
    }
    getSpans<RefSpan>().forEach { span ->
        boundaries.add(getSpanStart(span))
        boundaries.add(getSpanEnd(span))
    }
    getSpans<QuranRefSpan>().forEach { span ->
        boundaries.add(getSpanStart(span))
        boundaries.add(getSpanEnd(span))
    }

    val parts = mutableListOf<HadithRichTextPart>()
    val indices = boundaries.toList()

    for (index in 0 until indices.size - 1) {
        val start = indices[index]
        val end = indices[index + 1]
        if (start >= end) continue

        val slice = content.substring(start, end)
        if (slice.isEmpty()) continue

        val mid = (start + end) / 2

        getSpans(mid, mid, RefSpan::class.java)
            .firstOrNull()
            ?.let { span ->
                appendPart(
                    parts,
                    HadithRichTextPart.HadithRef(
                        text = slice,
                        id = span.id,
                    ),
                )
                continue
            }

        getSpans(mid, mid, QuranRefSpan::class.java)
            .firstOrNull()
            ?.let { span ->
                appendPart(
                    parts,
                    HadithRichTextPart.QuranRef(
                        text = slice,
                        chapter = span.chapter.toIntOrNull() ?: 0,
                        verses = span.verses,
                    ),
                )
                continue
            }

        val styleSpans = getSpans(mid, mid, StyleSpan::class.java)
        val bold = styleSpans.any {
            it.style == Typeface.BOLD || it.style == Typeface.BOLD_ITALIC
        }

        val italic = styleSpans.any {
            it.style == Typeface.ITALIC || it.style == Typeface.BOLD_ITALIC
        }

        val underline = getSpans(mid, mid, UnderlineSpan::class.java).isNotEmpty()

        appendPart(
            parts,
            HadithRichTextPart.Plain(
                text = slice,
                bold = bold,
                italic = italic,
                underline = underline,
            ),
        )
    }

    return parts
}

private fun appendPart(parts: MutableList<HadithRichTextPart>, next: HadithRichTextPart) {
    when (val previous = parts.lastOrNull()) {
        is HadithRichTextPart.Plain if next is HadithRichTextPart.Plain && previous.bold == next.bold && previous.italic == next.italic && previous.underline == next.underline -> {
            parts[parts.lastIndex] = previous.copy(text = previous.text + next.text)
        }

        is HadithRichTextPart.HadithRef if next is HadithRichTextPart.HadithRef && previous.id == next.id -> {
            parts[parts.lastIndex] = previous.copy(text = previous.text + next.text)
        }

        is HadithRichTextPart.QuranRef if next is HadithRichTextPart.QuranRef && previous.chapter == next.chapter && previous.verses == next.verses -> {
            parts[parts.lastIndex] = previous.copy(text = previous.text + next.text)
        }

        else -> parts += next
    }
}
