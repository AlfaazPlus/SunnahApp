package com.alfaazplus.sunnah.ui.components.reader

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.HadithNavigationItem
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel


private fun getPreviousHadith(currentHadithId: String?, items: List<HadithNavigationItem>): Pair<String, String>? {
    val currentIndex = items.indexOfFirst { it.hadithId == currentHadithId }
    if (currentIndex <= 0) return null

    val item = items[currentIndex - 1]
    return Pair(item.hadithId, item.visibleNumbering)
}

private fun getNextHadith(currentHadithId: String?, items: List<HadithNavigationItem>): Pair<String, String>? {
    val currentIndex = items.indexOfFirst { it.hadithId == currentHadithId }
    if (currentIndex == -1 || currentIndex >= items.lastIndex) return null

    val item = items[currentIndex + 1]
    return Pair(item.hadithId, item.visibleNumbering)
}

@Composable
fun HorizontalReaderBottomBar(readerVm: ReaderViewModel, pagerState: PagerState) {
    val currentHadithId by readerVm.activeHadithId.collectAsStateWithLifecycle()
    val items by readerVm.hadithNavigationItems.collectAsStateWithLifecycle()

    val previousHadith = getPreviousHadith(currentHadithId, items)
    val nextHadith = getNextHadith(currentHadithId, items)

    Surface(
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                color = colorScheme.outline.alpha(0.2f)
            )

            BottomAppBar(
                containerColor = colorScheme.surfaceContainer,
                tonalElevation = 0.dp,
                contentPadding = PaddingValues(0.dp),
            ) {
                NavigationButton(
                    hadithNumber = previousHadith?.second,
                    modifier = Modifier.weight(1f),
                    isPrevious = true,
                ) {
                    previousHadith?.let {
                        readerVm.requestHadithNavigation(it.first)
                    }
                }

                NavigationButton(
                    hadithNumber = nextHadith?.second,
                    modifier = Modifier.weight(1f),
                    isPrevious = false,
                ) {
                    nextHadith?.let {
                        readerVm.requestHadithNavigation(it.first)
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationButton(
    hadithNumber: String?,
    modifier: Modifier = Modifier,
    isPrevious: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = colorScheme.background
    val txtColor = colorScheme.onBackground

    Card(
        modifier = modifier
            .height(50.dp)
            .padding(horizontal = 10.dp)
            .alpha(if (hadithNumber != null) 1f else 0.5f),
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = txtColor,
        ),
        shape = MaterialTheme.shapes.small,
        enabled = hadithNumber != null,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isPrevious) {
                ReaderNavigationButtonIcon(
                    icon = R.drawable.ic_arrow_left,
                    label = null,
                )
            }

            if (hadithNumber != "book") {
                Text(
                    text = if (isPrevious) stringResource(R.string.previousHadith) else stringResource(R.string.nextHadith),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Text(
                    text = if (isPrevious) stringResource(R.string.previousBook)
                    else stringResource(R.string.nextBook),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (!isPrevious) {
                ReaderNavigationButtonIcon(
                    icon = R.drawable.ic_arrow_right,
                    label = null,
                )
            }
        }
    }
}

@Composable
fun ReaderNavigationButtonIcon(
    @DrawableRes
    icon: Int,
    label: String?,
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = label,
        modifier = Modifier
            .width(14.dp)
            .height(14.dp),
        tint = colorScheme.onSurface,
    )
}
