package com.alfaazplus.sunnah.ui.utils.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.FractionRes
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.alfaazplus.sunnah.R
import com.google.android.material.color.MaterialColors

fun Context.getPackageNameRelease(): String {
    return packageName.replace(".debug", "")
}

fun Context.isRTL() = getBoolean(R.bool.isRTL)

fun Context.getBoolean(@BoolRes boolResId: Int): Boolean = resources.getBoolean(boolResId)

fun Context.getStringArray(@ArrayRes arrayResId: Int): Array<String?> = resources.getStringArray(
    arrayResId
)

fun Context.getIntArray(@ArrayRes arrayResId: Int): IntArray = resources.getIntArray(arrayResId)

fun Context.getTypedArray(@ArrayRes arrayResId: Int): TypedArray = resources.obtainTypedArray(
    arrayResId
)

fun Context.getFont(@FontRes fontResId: Int): Typeface? {
    return try {
        ResourcesCompat.getFont(this, fontResId)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.drawable(@DrawableRes drawableResId: Int): Drawable {
    return AppCompatResources.getDrawable(this, drawableResId)!!
}

fun Context.copyToClipboard(text: CharSequence): Boolean {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    clipboard?.let {
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        return true
    }
    return false
}