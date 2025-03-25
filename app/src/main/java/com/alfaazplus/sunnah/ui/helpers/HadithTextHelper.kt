package com.alfaazplus.sunnah.ui.helpers

import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.ui.utils.text.HadithTagHandler
import com.alfaazplus.sunnah.ui.utils.text.HtmlParser
import com.alfaazplus.sunnah.ui.utils.text.toHadithAnnotatedString

object HadithTextHelper {
    fun prepareText(text: String): CharSequence {
        return text.parseAsHtml(tagHandler = HtmlParser(HadithTagHandler()))
    }
}