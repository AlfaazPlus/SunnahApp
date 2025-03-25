package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
private fun RowScope.LayoutButton(
    icon: Int,
    label: Int,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .padding(8.dp)
                .height(120.dp),
            onClick = onClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(60.dp)
                )
            }
        }

        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun LayoutOptionSheet(isOpen: Boolean, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val selectedLayoutOption = ReaderUtils.getHadithLayoutOption()
    fun onSelected(option: String) {
        if (option == selectedLayoutOption) return

        coroutineScope.launch {
            DataStoreManager.write(stringPreferencesKey(Keys.HADITH_LAYOUT), option)

            withContext(Dispatchers.Main) {
                onDismiss()
            }
        }
    }

    BottomSheet(
        isOpen = isOpen,
        onDismiss = onDismiss,
        icon = R.drawable.ic_square_menu,
        title = stringResource(R.string.hadith_layout),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LayoutButton(
                R.drawable.ic_carousel_horizontal, R.string.horizontal, isSelected = selectedLayoutOption == ReaderUtils.HADITH_LAYOUT_HORIZONTAL
            ) {
                onSelected(ReaderUtils.HADITH_LAYOUT_HORIZONTAL)
            }
            LayoutButton(
                R.drawable.ic_carousel_vertical, R.string.vertical, isSelected = selectedLayoutOption == ReaderUtils.HADITH_LAYOUT_VERTICAL
            ) {
                onSelected(ReaderUtils.HADITH_LAYOUT_VERTICAL)
            }
        }
    }
}