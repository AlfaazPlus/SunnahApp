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
    collectionId: String,
    height: Dp = 55.dp,
) {
    fun resolveCollectionIcon(collectionId: String): Int {
        return when (collectionId) {
            "bukhari" -> R.drawable.book_bukhari
            "muslim" -> R.drawable.book_muslim
            "nasai" -> R.drawable.book_nasai
            "abudawud" -> R.drawable.book_abudawud
            "tirmidhi" -> R.drawable.book_tirmidhi
            "ibnmajah" -> R.drawable.book_ibnmajah
            "malik" -> R.drawable.book_malik
            "riyadussalihin" -> R.drawable.book_riyadussalihin
            "forty" -> R.drawable.book_forty
            else -> 0
        }
    }

    val iconId = resolveCollectionIcon(collectionId)

    if (iconId == 0) return

    Image(
        painter = painterResource(resolveCollectionIcon(collectionId)),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .height(height)
            .padding(4.dp)
    )
}
