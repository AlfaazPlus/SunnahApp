package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun TextIconButton(
    painter: Painter,
    contentDescription: String? = null,
    text: String,
    tint: Color = LocalContentColor.current,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = tint,
    ),
    shape: RoundedCornerShape = RoundedCornerShape(100),
    border: BorderStroke? = null,
    enabled: Boolean = true,
    small: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(
            if (small) 36.dp else 48.dp,
        ),
        shape = shape,
        contentPadding = PaddingValues(horizontal = if (small) 12.dp else 16.dp),
        enabled = enabled, colors = colors, border = border,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(
                if (small) 20.dp else 24.dp,
            ),
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 10.dp),
            style = if (small) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge,
            color = tint,
        )
    }
}