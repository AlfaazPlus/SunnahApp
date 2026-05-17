package com.alfaazplus.sunnah.ui.utils.text

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import com.alfaazplus.sunnah.ui.components.reader.HadithActions
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption

data class ComposeUiConfig(
    val context: Context,
    val colors: ColorScheme,
    val type: Typography,
)

data class TextBuilderParams(
    val uiConfig: ComposeUiConfig,
    val hadithActions: HadithActions,
    val hadithTextOption: HadithTextOption,
    val isSanadEnabled: Boolean,
    val arabicSizePercent: Int,
    val translationSizePercent: Int,
    val isSerifFontStyle: Boolean,
)

data class TranslationTextStyleParams(
    val colors: ColorScheme,
    val type: Typography,
    val isSerif: Boolean,
    val sizePercent: Int,
)

data class ArabicTextStyleParams(
    val colors: ColorScheme,
    val type: Typography,
    val sizePercent: Int,
)

fun getTranslationTextStyle(
    params: TranslationTextStyleParams,
): TextStyle {
    val baselineFontSize = params.type.bodyLarge.fontSize
    val resolvedFontSize = baselineFontSize * params.sizePercent / 100

    return TextStyle(
        fontFamily = if (params.isSerif) FontFamily.Serif else FontFamily.SansSerif,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        ),
        fontSize = resolvedFontSize,
        lineHeight = resolvedFontSize * 1.5f,
    )
}

fun getArabicTextStyle(
    params: ArabicTextStyleParams,
): TextStyle {
    val baselineFontSize = params.type.bodyLarge.fontSize * 1.2 // slightly size for arabic texts
    val resolvedFontSize = baselineFontSize * params.sizePercent / 100

    return params.type.headlineSmall.copy(
        fontFamily = fontUthmani,
        fontSize = resolvedFontSize,
        textDirection = TextDirection.Rtl,
        lineHeight = resolvedFontSize * 1.8f,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Tight,
        )
    )
}

fun textStyleForLang(
    langCode: String,
    colors: ColorScheme,
    type: Typography,
    arabicSizePercent: Int,
    translationSizePercent: Int,
    isSerifFontStyle: Boolean,
): TextStyle {
    return if (langCode == "ar") {
        getArabicTextStyle(
            ArabicTextStyleParams(
                colors = colors,
                type = type,
                sizePercent = arabicSizePercent,
            ),
        )
    } else {
        getTranslationTextStyle(
            TranslationTextStyleParams(
                colors = colors,
                type = type,
                sizePercent = translationSizePercent,
                isSerif = isSerifFontStyle,
            ),
        )
    }
}

fun buildStyledHadithAnnotatedString(
    text: String,
    langCode: String,
    colors: ColorScheme,
    type: Typography,
    arabicSizePercent: Int,
    translationSizePercent: Int,
    isSerifFontStyle: Boolean,
    actions: HadithActions,
): AnnotatedString {
    val raw = buildHadithAnnotatedString(
        text = text,
        linkColor = colors.primary,
        actions = actions,
    )

    val textStyle = textStyleForLang(
        langCode = langCode,
        colors = colors,
        type = type,
        arabicSizePercent = arabicSizePercent,
        translationSizePercent = translationSizePercent,
        isSerifFontStyle = isSerifFontStyle,
    )

    return buildAnnotatedString {
        withStyle(textStyle.toParagraphStyle()) {
            withStyle(textStyle.toSpanStyle()) {
                append(raw)
            }
        }
    }
}
