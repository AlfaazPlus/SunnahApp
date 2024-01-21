package com.alfaazplus.sunnah.ui.utils.text

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView
import com.alfaazplus.sunnah.ui.utils.text.span.RoundedBackgroundSpan

class SelectableLinkMovementMethod : LinkMovementMethod() {
    private var bgSpan: RoundedBackgroundSpan? = null
    override fun onTouchEvent(textView: TextView, spannable: Spannable, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                getPressedSpan(textView, spannable, event)?.let {
                    it.pressed = true
                    Selection.setSelection(spannable, spannable.getSpanStart(it), spannable.getSpanEnd(it))
                    it
                }.also { bgSpan = it }
            }

            MotionEvent.ACTION_UP -> {
                bgSpan?.let {
                    it.pressed = false
                    Selection.removeSelection(spannable)
                    bgSpan = null
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val touchedSpan = getPressedSpan(textView, spannable, event)
                bgSpan.takeIf { touchedSpan != it }?.let {
                    it.pressed = false
                    Selection.removeSelection(spannable)
                    bgSpan = null
                }
            }

            else -> {
                bgSpan?.pressed = false
                bgSpan = null
                Selection.removeSelection(spannable)
            }
        }

        return super.onTouchEvent(textView, spannable, event)
    }

    private fun getPressedSpan(textView: TextView, spannable: Spannable, event: MotionEvent): RoundedBackgroundSpan? {
        val x = event.x.toInt() - textView.totalPaddingLeft + textView.scrollX
        val y = event.y.toInt() - textView.totalPaddingTop + textView.scrollY
        val layout = textView.layout
        val position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x.toFloat())
        val link = spannable.getSpans(position, position, RoundedBackgroundSpan::class.java)

        var touchedSpan: RoundedBackgroundSpan? = null

        if (link.isNotEmpty() && positionWithinTag(position, spannable, link[0])) {
            touchedSpan = link[0]
        }

        return touchedSpan
    }

    private fun positionWithinTag(position: Int, spannable: Spannable, tag: Any): Boolean {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag)
    }
}