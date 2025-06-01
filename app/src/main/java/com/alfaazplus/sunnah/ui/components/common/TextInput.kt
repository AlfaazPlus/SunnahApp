package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alfaazplus.sunnah.ui.theme.alpha

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    bgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    enabled: Boolean = true,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        maxLines = maxLines,
        minLines = minLines,
        label = if (label != null) {
            { Text(text = label) }
        } else null,
        placeholder = if (placeholder != null) {
            { Text(text = placeholder) }
        } else null,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = bgColor,
            focusedContainerColor = bgColor,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.alpha(0.6f),
        ),
        enabled = enabled,
    )
}