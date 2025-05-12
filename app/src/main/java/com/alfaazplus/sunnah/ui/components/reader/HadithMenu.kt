package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu

@Composable
private fun Item(
    text: String,
    icon: Int,
    onClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.background,
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun Items(
    onClose: () -> Unit,
) {

    // TODO: Add actions
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Item(
                text = "Add to Bookmark",
                icon = R.drawable.ic_bookmark_plus,
            ) {
                onClose()
            }
        }
        item {
            Item(
                text = "Add to Collection",
                icon = R.drawable.ic_library,
            ) {
                onClose()
            }
        }
        item {
            Item(
                text = "Share this Hadith",
                icon = R.drawable.ic_share,
            ) {
                onClose()
            }
        }
        item {
            Item(
                text = "Copy Hadith Text",
                icon = R.drawable.ic_clipboard,
            ) {
                onClose()
            }
        }
        item {
            Item(
                text = "Report an Issue",
                icon = R.drawable.ic_flag,
            ) {
                onClose()
            }
        }
    }
}

@Composable
fun HadithMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
) {
    BottomSheetMenu(
        title = "Hadith Options",
        isOpen = isOpen,
        onDismiss = onClose,
    ) {
        Items(onClose)
    }
}