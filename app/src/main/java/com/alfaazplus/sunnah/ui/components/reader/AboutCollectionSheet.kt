package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutCollectionSheet(cwi: CollectionWithInfo) {
    var showSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .clip(shapes.small)
            .background(color = colorScheme.surfaceVariant)
            .clickable { showSheet = true }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .alpha(0.8f), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            tint = colorScheme.onSurface,
            modifier = Modifier
                .padding(end = 5.dp)
                .size(16.dp)
        )
        Text(
            text = stringResource(R.string.about_collection),
            style = typography.labelMedium,
        )
    }

    BottomSheet(
        isOpen = showSheet,
        onDismiss = {
            showSheet = false
        },
    ) {
        Text(
            text = cwi.info!!.intro!!
                .parseAsHtml()
                .toString()
                .replace("\\n", "\n")
                .replace("\\r", "\r"),
            modifier = Modifier
                .padding(20.dp)
                .padding(bottom = 100.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}