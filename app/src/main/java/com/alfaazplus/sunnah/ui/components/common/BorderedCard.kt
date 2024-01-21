package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.theme.alpha


@Composable
fun BorderedCard(
    padding: PaddingValues = PaddingValues(15.dp),
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .border(2.dp, MaterialTheme.colorScheme.primary.alpha(0.2f), MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(padding)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
        content = content,
    )
}