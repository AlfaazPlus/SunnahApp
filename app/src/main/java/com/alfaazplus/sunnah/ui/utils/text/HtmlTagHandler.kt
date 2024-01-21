package com.alfaazplus.sunnah.ui.utils.text

import android.text.Editable
import org.xml.sax.Attributes

interface HtmlTagHandler {
    fun handleTag(opening: Boolean, tag: String?, output: Editable?, attributes: Attributes?): Boolean
}
