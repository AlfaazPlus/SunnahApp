package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.components.settings.SettingsItemContent

@Composable
fun CheckboxItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Checkbox(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp),
            enabled = enabled,
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        SettingsItemContent(
            titleStr = title, subtitleStr = subtitle, modifier = Modifier
                .weight(1f)
                .alpha(if (enabled) 1f else 0.6f),
        )
    }
}