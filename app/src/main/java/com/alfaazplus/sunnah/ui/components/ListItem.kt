package com.alfaazplus.sunnah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R

@Composable
fun ListItemIcon(
    icon: Int,
    contentDescription: String? = null
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        modifier = Modifier.padding(end = 15.dp)
    )
}

@Composable
fun ListItemContent(
    title: Int? = null,
    titleStr: String? = null,
    subtitle: Int? = null,
    subtitleStr: String? = null,
    modifier: Modifier
) {
    val titleText = titleStr ?: if (title != null) stringResource(title) else null
    val subtitleText = subtitleStr ?: if (subtitle != null) stringResource(subtitle) else null

    Column(modifier = modifier) {
        if (titleText != null) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleSmall
            )
        }
        if (subtitleText != null) {
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(top = 3.dp)
                    .alpha(0.75f),
            )
        }
    }
}

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: Int,
    subtitle: Int? = null,
    subtitleStr: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .shadow(2.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) leading()

            ListItemContent(title = title, subtitle = subtitle, subtitleStr = subtitleStr, modifier = Modifier.weight(1f))

            if (trailing != null) {
                trailing()
            }
        }
    }
}