package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    text: String,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: RoundedCornerShape = RoundedCornerShape(100),
    border: BorderStroke? = null,
    enabled: Boolean = true,
    small: Boolean = false,
    fullWidth: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(
                if (small) 36.dp else 48.dp,
            )
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier),
        shape = shape,
        contentPadding = PaddingValues(horizontal = if (small) 12.dp else 16.dp),
        enabled = enabled, colors = colors, border = border,
    ) {
        Text(
            text = text,
            style = if (small) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge,
        )
    }
}