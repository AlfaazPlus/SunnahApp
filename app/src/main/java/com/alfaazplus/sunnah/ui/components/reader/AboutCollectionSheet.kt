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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutCollectionSheet(cwi: CollectionWithInfo) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(true)

    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .clip(MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .clickable { showSheet = true }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .alpha(0.8f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(end = 5.dp)
                .size(16.dp)
        )
        Text(
            text = "About this collection",
            style = MaterialTheme.typography.labelMedium,
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
        ) {
            Text(
                text = cwi.info!!.intro!!.parseAsHtml().toString().replace("\\n", "\n").replace("\\r", "\r"),
                modifier = Modifier
                    .padding(20.dp)
                    .padding(bottom = 100.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}