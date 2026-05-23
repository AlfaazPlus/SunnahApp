package com.alfaazplus.sunnah.ui.components.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.theme.alpha

@Composable
fun SectionEmptyMessage(
    message: String,
    horizontalPadding: Dp = 16.dp,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = horizontalPadding)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.alpha(0.6f), MaterialTheme.shapes.medium)
            .padding(20.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SectionHeaderActionButton(
    text: String,
    icon: Int? = null,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.height(28.dp),
        contentPadding = PaddingValues(
            start = if (icon != null) 6.dp else 10.dp,
            end = 10.dp,
            top = 0.dp,
            bottom = 0.dp,
        ),
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(16.dp),
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
fun SectionHeaderViewAll(
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.alpha(0.3f),
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.height(28.dp),
        contentPadding = PaddingValues(
            start = 10.dp,
            end = 5.dp,
            top = 0.dp,
            bottom = 0.dp,
        ),
    ) {
        Text(
            text = "View all",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(16.dp),
        )
    }
}
