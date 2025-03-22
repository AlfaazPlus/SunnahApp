package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R

@Composable
fun NavigationButtonIcon(
    icon: ImageVector,
    label: String,
    modifier: Modifier,
) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        modifier = modifier
            .width(14.dp)
            .height(14.dp)
    )
}

@Composable
fun NavigationButton(
    hadithNumber: String?,
    modifier: Modifier = Modifier,
    isPrevious: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .height(50.dp)
            .padding(horizontal = 10.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                enabled = hadithNumber != null,
            ) { onClick() }
            .alpha(if (hadithNumber != null) 1f else 0.5f)
    ) {
        if (hadithNumber != null) {
            Text(
                text = if (isPrevious && hadithNumber == "book") stringResource(R.string.previousBook)
                else if (!isPrevious && hadithNumber == "book") stringResource(R.string.nextBook)
                else hadithNumber,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isPrevious) {
                NavigationButtonIcon(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    label = stringResource(R.string.previousHadith),
                    modifier = Modifier
                        .padding(end = 4.dp)
                )
            }
            if (hadithNumber != "book") {
                Text(
                    text = if (isPrevious) stringResource(R.string.previousHadith) else stringResource(R.string.nextHadith),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                )
            }
            if (!isPrevious) {
                NavigationButtonIcon(
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    label = stringResource(R.string.nextHadith),
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ReaderBottomBar(
    prevHadithNumber: String?,
    nextHadithNumber: String?,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.height(64.dp)
    ) {
        NavigationButton(
            hadithNumber = prevHadithNumber,
            modifier = Modifier.weight(1f),
            isPrevious = true,
        ) { onPreviousClick() }
        NavigationButton(
            hadithNumber = nextHadithNumber,
            modifier = Modifier.weight(1f),
            isPrevious = false,
        ) { onNextClick() }
    }
}