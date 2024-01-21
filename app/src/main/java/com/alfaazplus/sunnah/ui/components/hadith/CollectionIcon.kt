package com.alfaazplus.sunnah.ui.components.hadith

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R

@Composable
fun CollectionIcon(
    collectionId: Int = 0,
    height: Dp = 55.dp,
) {
    fun resolveCollectionIcon(collectionId: Int): Int {
        return when (collectionId) {
            1 -> return R.drawable.book_bukhari
            2 -> return R.drawable.book_muslim
            3 -> return R.drawable.book_nasai
            4 -> return R.drawable.book_abudawud
            5 -> return R.drawable.book_tirmidhi
            6 -> return R.drawable.book_ibnmajah
            else -> 0
        }
    }

    Image(
        painter = painterResource(resolveCollectionIcon(collectionId)),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .height(height)
            .padding(4.dp)
    )
}