package com.alfaazplus.sunnah.ui.utils.text

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.alfaazplus.sunnah.ui.components.reader.HadithActions
import com.alfaazplus.sunnah.ui.theme.fontUrdu
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.StringUtils
import com.alfaazplus.sunnah.ui.utils.app.isUrduLanguageCode
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils

data class ComposeUiConfig(
    val context: Context,
    val colors: ColorScheme,
)

data class TextBuilderParams(
    val translationId: String,
    val uiConfig: ComposeUiConfig,
    val hadithActions: HadithActions,
    val hadithTextOption: HadithTextOption,
    val isSanadEnabled: Boolean,
    val arabicSizePercent: Int,
    val translationSizePercent: Int,
    val isSerifFontStyle: Boolean,
)

data class TranslationTextStyleParams(
    val translationId: String,
    val isSerif: Boolean,
    val sizePercent: Int,
)

data class ArabicTextStyleParams(
    val sizePercent: Int,
)

fun getTranslationTextStyle(
    params: TranslationTextStyleParams,
): TextStyle {
    val baselineFontSize = 16.sp
    val resolvedFontSize = baselineFontSize * params.sizePercent / 100
    val langCode = TranslationUtils.langCodeFromId(params.translationId)
    val isUrdu = langCode.isUrduLanguageCode()
    val lineHeightMultiplier = if (isUrdu) 2.5f else 1.5f

    return TextStyle(
        fontFamily = if (isUrdu) fontUrdu
        else if (params.isSerif) FontFamily.Serif
        else FontFamily.SansSerif,
        textDirection = textDirectionForLang(langCode),
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        ),
        fontSize = resolvedFontSize,
        lineHeight = resolvedFontSize * lineHeightMultiplier,
    )
}

fun getArabicTextStyle(
    params: ArabicTextStyleParams,
): TextStyle {
    val baselineFontSize = 20.sp
    val resolvedFontSize = baselineFontSize * params.sizePercent / 100

    return TextStyle(
        fontFamily = fontUthmani,
        fontSize = resolvedFontSize,
        textDirection = TextDirection.Rtl,
        lineHeight = resolvedFontSize * 1.8f,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Tight,
        ),
    )
}

fun textDirectionForLang(
    langCode: String,
): TextDirection {
    return if (StringUtils.isRtlLanguage(langCode)) TextDirection.Rtl else TextDirection.Ltr
}


fun textStyleForLang(
    langCode: String,
    sizePercent: Int = 100,
    isSerifFontStyle: Boolean = false,
): TextStyle {
    return if (langCode == "ar") {
        getArabicTextStyle(
            ArabicTextStyleParams(
                sizePercent = sizePercent,
            ),
        )
    } else {
        getTranslationTextStyle(
            TranslationTextStyleParams(
                translationId = langCode,
                sizePercent = sizePercent,
                isSerif = isSerifFontStyle,
            ),
        )
    }
}

fun buildStyledHadithAnnotatedString(
    text: String,
    langCode: String,
    colors: ColorScheme,
    sizePercent: Int,
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
        sizePercent = sizePercent,
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
