package com.alfaazplus.sunnah.ui.utils.text

import android.text.Editable
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import com.alfaazplus.sunnah.Logger
import org.xml.sax.Attributes

class HadithTagHandler : HtmlTagHandler {
    private var narratorTagStartIndex: Int? = null
    private var narratorId: Int? = null

    private var referenceTagStartIndex: Int? = null
    private var referenceChapterNo: Int? = null
    private var referenceVerses: String? = null

    override fun handleTag(
        opening: Boolean,
        tag: String?,
        output: Editable?,
        attributes: Attributes?
    ): Boolean {
        if (output == null) {
            return false
        }

        return when (tag) {
//            "nr" -> handleNarrator(opening, output, attributes)
            "ref" -> handleReference(opening, output, attributes)
            else -> false
        }
    }

    private fun handleNarrator(opening: Boolean, output: Editable, attributes: Attributes?): Boolean {
        if (opening && attributes != null) {
            narratorTagStartIndex = output.length
            narratorId = attributes.getValue("id").toInt()
        } else if (!opening && narratorTagStartIndex != null && narratorId != null) {
            setClickSpan(output, narratorTagStartIndex!!, output.length, {
                val narratorId = (it as Array<*>)[0] as Int
            }, narratorId!!)

            narratorTagStartIndex = null
            narratorId = null
        }
        return true
    }

    private fun handleReference(opening: Boolean, output: Editable, attributes: Attributes?): Boolean {
        if (opening && attributes != null) {
            referenceTagStartIndex = output.length
            referenceChapterNo = attributes.getValue("chapter").toInt()
            referenceVerses = attributes.getValue("verses")
        } else if (!opening && referenceTagStartIndex != null && referenceChapterNo != null && referenceVerses != null) {
            output.setSpan(URLSpan("${referenceChapterNo!!}:${referenceVerses}"), referenceTagStartIndex!!, output.length, SPAN_EXCLUSIVE_EXCLUSIVE)

            /*setClickSpan(output, referenceTagStartIndex!!, output.length, {
                val chapterNo = (it as Array<*>)[0] as Int
                val versesStr = it[1] as String

                val verses = versesStr.split("-")
                val fromVerse = verses[0].toInt()
                var toVerse = fromVerse

                if (verses.size > 1) {
                    toVerse = verses[1].toInt()
                }

                NavigationHelper.openQuranReference(context, QuranReference(chapterNo, fromVerse, toVerse))
            }, referenceChapterNo!!, referenceVerses!!)*/

            referenceTagStartIndex = null
            referenceChapterNo = null
            referenceVerses = null
        }

        return true
    }

    private fun setClickSpan(output: Editable, start: Int, end: Int, clickCallback: (params: Any) -> Unit, vararg params: Any) {
        output.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    clickCallback(params)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            },
            start,
            end,
            SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}
