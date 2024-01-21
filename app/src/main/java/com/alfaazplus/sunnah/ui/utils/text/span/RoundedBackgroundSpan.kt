/*
 * Copyright (c) Faisal Khan (https://github.com/faisalcodes)
 * Created on 23/2/2022.
 * All rights reserved.
 */
package com.alfaazplus.sunnah.ui.utils.text.span

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.text.style.ReplacementSpan

class RoundedBackgroundSpan(
    private val bgColor: Int = Color.TRANSPARENT,
    private val bgColorPressed: Int = bgColor,
    private val textColor: Int = -1,
    private var radius: Int = 10,
) : ReplacementSpan() {
    var paddingH: Int = 4
    var pressed = false

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        return (paddingH + paint.measureText(text.subSequence(start, end).toString()) + paddingH).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val initialTxtColor = paint.color
        val width = paint.measureText(text.subSequence(start, end).toString())

        paint.color = if (pressed) bgColorPressed else bgColor

        RectF(x, top.toFloat(), x + width + paddingH.shl(1), bottom.toFloat()).let {
            if (radius != 0) canvas.drawRoundRect(it, radius.toFloat(), radius.toFloat(), paint)
            else canvas.drawRect(it, paint)
        }

        paint.color = if (textColor != -1) textColor else initialTxtColor
        canvas.drawText(text, start, end, x + paddingH, y.toFloat(), paint)
    }
}