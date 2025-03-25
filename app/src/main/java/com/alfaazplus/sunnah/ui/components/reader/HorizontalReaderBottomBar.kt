package com.alfaazplus.sunnah.ui.components.reader

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R

@Composable
private fun NavigationButtonIcon(
    @DrawableRes icon: Int,
    label: String,
    modifier: Modifier,
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = label,
        modifier = modifier
            .width(14.dp)
            .height(14.dp),
        tint = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun NavigationButton(
    hadithNumber: String?,
    modifier: Modifier = Modifier,
    isPrevious: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(50.dp)
            .padding(horizontal = 10.dp)
            .alpha(if (hadithNumber != null) 1f else 0.5f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        shape = MaterialTheme.shapes.small,
        enabled = hadithNumber != null,
        onClick = onClick,

        ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (hadithNumber != null) {
                Text(
                    text = if (isPrevious && hadithNumber == "book") stringResource(R.string.previousBook)
                    else if (!isPrevious && hadithNumber == "book") stringResource(R.string.nextBook)
                    else hadithNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isPrevious) {
                    NavigationButtonIcon(
                        icon = R.drawable.ic_arrow_left, label = stringResource(R.string.previousHadith), modifier = Modifier.padding(end = 4.dp)
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
                        icon = R.drawable.ic_arrow_right, label = stringResource(R.string.nextHadith), modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalReaderBottomBar(
    prevHadithNumber: String?,
    nextHadithNumber: String?,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Surface(
        shadowElevation = 12.dp,
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
}