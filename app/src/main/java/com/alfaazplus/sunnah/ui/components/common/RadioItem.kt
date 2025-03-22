package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.components.settings.SettingsItemContent

@Composable
fun RadioItem(
    modifier: Modifier = Modifier,
    title: Int,
    subtitle: Int? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        SettingsItemContent(
            title = title,
            subtitle = subtitle,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp),
            selected = selected,
            onClick = onClick,
        )
    }
}