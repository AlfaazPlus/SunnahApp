package com.alfaazplus.sunnah.ui.helpers

import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.ui.utils.text.HadithTagHandler
import com.alfaazplus.sunnah.ui.utils.text.HtmlParser

object HadithTextHelper {
    fun prepareText(text: String): CharSequence {
        return text.replace("\n", "<br/>").parseAsHtml(tagHandler = HtmlParser(HadithTagHandler()))
    }
}