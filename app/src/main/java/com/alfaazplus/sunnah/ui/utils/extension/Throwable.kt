package com.alfaazplus.sunnah.ui.utils.extension

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.getStackTraceString(): String {
    val writer = StringWriter()
    val printWriter = PrintWriter(writer)
    this.printStackTrace(printWriter)
    return writer.toString()
}