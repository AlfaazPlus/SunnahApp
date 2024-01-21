package com.alfaazplus.sunnah.ui.components.settings

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
import com.alfaazplus.sunnah.ui.components.ListItem

@Composable
fun SettingsItemArrow() {
    Icon(
        painter = painterResource(id = R.drawable.ic_chevron_right),
        contentDescription = null,
        modifier = Modifier.padding(start = 15.dp)
    )
}

@Composable
fun SettingsItemIcon(
    icon: Int,
    contentDescription: String
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        modifier = Modifier.padding(end = 15.dp)
    )
}

@Composable
fun SettingsItemContent(
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
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: Int,
    subtitle: Int? = null,
    subtitleStr: String? = null,
    icon: Int? = null,
    iconImage: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier,
        leading = {
            if (icon != null) SettingsItemIcon(icon = icon, contentDescription = stringResource(title))
            else if (iconImage != null) iconImage()
        },
        trailing = {
            SettingsItemArrow()
        },
        title = title,
        subtitle = subtitle,
        subtitleStr = subtitleStr,
        onClick = onClick
    )
}