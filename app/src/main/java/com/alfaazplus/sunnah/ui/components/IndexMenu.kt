package com.alfaazplus.sunnah.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.extension.verticalFadingEdge
import com.alfaazplus.sunnah.ui.utils.keys.Routes

private data class IndexMenuItemGroup(
    val items: List<IndexMenuItem>,
)

private data class IndexMenuItem(
    @param:DrawableRes
    val icon: Int,
    @param:StringRes
    val title: Int,

    val iconTint: Color? = null,
    val textColor: Color? = null,
    val onClick: (ctx: Context) -> Unit,
)

@Composable
private fun getItems(): List<IndexMenuItemGroup> {
    val navController = LocalNavHostController.current

    return listOf(
        IndexMenuItemGroup(
            listOf(
                IndexMenuItem(
                    R.drawable.ic_settings, R.string.settings,
                    onClick = {
                        navController.navigate(route = Routes.SETTINGS.arg(false))
                    },
                ),
            )
        ),
        IndexMenuItemGroup(
            listOf(
                IndexMenuItem(
                    R.drawable.ic_info, R.string.about_us,
                    onClick = {
                        navController.navigate(route = Routes.ABOUT_US)
                    },
                ),
                IndexMenuItem(
                    R.drawable.ic_star, R.string.rate_app,
                    onClick = {
                        NavigationHelper.openPlayStoreListing(it)
                    },
                ),
                IndexMenuItem(
                    R.drawable.ic_share, R.string.share_app,
                    onClick = {
                        NavigationHelper.shareApp(it)
                    },
                ),
                IndexMenuItem(
                    R.drawable.logo_quranapp, R.string.install_quranapp, iconTint = colorScheme.primary,
                    onClick = {
                        NavigationHelper.openQuranAppPlayStoreListing(it)
                    },
                ),
                IndexMenuItem(
                    R.drawable.ic_donate, R.string.donate, iconTint = colorScheme.primary,
                    onClick = {
                        NavigationHelper.openDonationPage(it)
                    },
                ),
            )
        ),
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexMenuButton() {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(true)

    val config = LocalConfiguration.current
    val sheetMaxWidth = config.screenWidthDp.dp * .95f
    val sheetWidthDiff = (config.screenWidthDp.dp - sheetMaxWidth) / 2

    SimpleTooltip(text = stringResource(R.string.menu)) {
        IconButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                showMenu = true
            },
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_hamburger
                ),
                contentDescription = stringResource(R.string.menu),
                tint = colorScheme.onSurface,
            )
        }
    }


    if (!showMenu) return

    ModalBottomSheet(
        modifier = Modifier.padding(
            bottom = sheetWidthDiff
        ),
        onDismissRequest = {
            showMenu = false
        },
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentColor = colorScheme.onSurface,
        shape = shapes.large,
        scrimColor = Color.Black.alpha(0.5f),
        dragHandle = null,
        sheetMaxWidth = minOf(sheetMaxWidth, BottomSheetDefaults.SheetMaxWidth),
        contentWindowInsets = {
            WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical)
        },
    ) {
        IndexMenuContent {
            showMenu = false
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun IndexMenuContent(
    onClose: () -> Unit,
) {
    val config = LocalConfiguration.current

    val maxMenuHeight = config.screenHeightDp.dp * 0.8f
    val scrollState = rememberScrollState()
    val items = getItems()

    Column(
        modifier = Modifier
            .heightIn(max = maxMenuHeight)
            .background(colorScheme.surface, RoundedCornerShape(20.dp))
            .border(
                1.dp, colorScheme.outlineVariant.alpha(0.5f), shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_hamburger), contentDescription = null, modifier = Modifier.size(25.dp)
            )

            Text(
                text = stringResource(id = R.string.menu),
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .weight(1f),
                style = typography.titleLarge,
            )

            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape), onClick = onClose
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = stringResource(id = R.string.close),
                )
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = colorScheme.outlineVariant,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalFadingEdge(scrollState, color = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(top = 12.dp, bottom = 20.dp)
            ) {
                items.forEachIndexed { groupIndex, group ->
                    group.items.forEachIndexed { _, item ->
                        IndexMenuItemRow(item, onClose)
                    }

                    if (groupIndex < items.lastIndex) {
                        HorizontalDivider(
                            thickness = 1.dp, color = colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndexMenuItemRow(
    item: IndexMenuItem,
    onClose: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                item.onClick(context)
                onClose()
            })
            .padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = item.iconTint ?: LocalContentColor.current
        )

        Spacer(modifier = Modifier.width(17.dp))

        Text(
            text = stringResource(id = item.title), color = item.textColor ?: colorScheme.onSurface, fontWeight = FontWeight.Medium, maxLines = 1
        )
    }
}
