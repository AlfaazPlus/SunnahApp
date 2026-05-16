package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithReferenceSheet(
    hadithId: String?,
    onClose: () -> Unit,
) {

    BottomSheet(
        isOpen = hadithId != null,
        onDismiss = onClose,
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            /*fixme ItemRow(title = "Reference", value = "${cwi.info?.name} ${hadith.hadith.hadithNumber}")
            ItemRow(title = "In-book reference", value = hadith.translation?.refInBook ?: "")
            if (englishReference != null) {
                ItemRow(
                    title = "English reference", value = englishReference
                )
            }
            if (uscMsaReference != null) {
                ItemRow(
                    modifier = Modifier.alpha(0.5f), title = "USC-MSA web (English) reference", value = "${uscMsaReference} (deprecated)"
                )
            }*/
        }
    }

}

@Composable
private fun ItemRow(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(120.dp),
        )
        Text(
            ":",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center,
        )
        SelectionContainer {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        context.copyToClipboard(value)
                    },
            )
        }
    }
}
