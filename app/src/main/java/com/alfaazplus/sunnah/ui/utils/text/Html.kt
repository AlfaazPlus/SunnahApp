package com.alfaazplus.sunnah.ui.utils.text


fun String.stripHtml(): String {
    if (isEmpty()) return ""

    // First strip HTML tags
    val sb = StringBuilder(length)
    var inTag = false
    var i = 0

    val n = length

    while (i < n) {
        val c = this[i]

        if (c == '<') {
            inTag = true
        } else if (c == '>') {
            inTag = false
        } else if (!inTag) {
            sb.append(c)
        }

        i++
    }

    val textWithoutTags = sb.toString()

    if (!textWithoutTags.contains('&')) {
        return textWithoutTags.trim()
    }

    // Then decode HTML entities
    val decoded = StringBuilder(textWithoutTags.length)

    i = 0

    val len = textWithoutTags.length

    while (i < len) {
        val c = textWithoutTags[i]

        if (c == '&') {
            val end = textWithoutTags.indexOf(';', i)

            if (end != -1 && end - i < 10) {
                val replacement = when (val entity = textWithoutTags.substring(i + 1, end)) {
                    "amp" -> "&"
                    "lt" -> "<"
                    "gt" -> ">"
                    "quot" -> "\""
                    "apos" -> "'"
                    "nbsp" -> " "
                    else -> {
                        if (entity.startsWith("#")) {
                            try {
                                if (entity.startsWith("#x") || entity.startsWith("#X")) {
                                    entity
                                        .substring(2)
                                        .toInt(16)
                                        .toChar()
                                        .toString()
                                } else {
                                    entity
                                        .substring(1)
                                        .toInt()
                                        .toChar()
                                        .toString()
                                }
                            } catch (_: Exception) {
                                "&$entity;"
                            }
                        } else {
                            "&$entity;"
                        }
                    }
                }

                decoded.append(replacement)
                i = end + 1
            } else {
                decoded.append(c)
                i++
            }
        } else {
            decoded.append(c)
            i++
        }
    }

    return decoded
        .toString()
        .trim()
}
