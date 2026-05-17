package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceType
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.viewModels.AppViewModel

private val referenceDisplayOrder = listOf(
    HadithReferenceType.SUNNAHCOM_REFERENCE,
    HadithReferenceType.APP_REFERENCE,
    HadithReferenceType.IN_BOOK_REFERENCE,
    HadithReferenceType.ENGLISH_TRANSLATION,
    HadithReferenceType.ARABIC_ENGLISH_BOOK_REFERENCE,
    HadithReferenceType.ARABIC_REFERENCE,
    HadithReferenceType.US_MSA_REFERENCE,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithReferenceSheet(
    hadithId: String?,
    onClose: () -> Unit,
) {
    BottomSheet(
        isOpen = hadithId != null,
        onDismiss = onClose,
        title = stringResource(R.string.hadith_references),
    ) {
        if (hadithId != null) {
            Content(hadithId)
        }
    }
}

@Composable
private fun Content(
    hadithId: String,
    appVm: AppViewModel = hiltViewModel(),
) {
    val references by produceState<List<HadithReferenceEntity>?>(null, hadithId) {
        value = appVm.repo.dao.getReferencesForHadith(hadithId)
    }

    when (val refs = references) {
        null -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Loader()
        }

        else -> {
            val ordered = referenceDisplayOrder.mapNotNull { type ->
                refs.firstOrNull { it.type == type }
            }

            SelectionContainer {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (ordered.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_references),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        )
                    } else {
                        ordered.forEach { ref ->
                            ItemRow(
                                title = ref.label(),
                                value = ref.displayValue(),
                                modifier = if (ref.isDeprecated()) {
                                    Modifier.alpha(0.5f)
                                } else {
                                    Modifier
                                },
                            )
                        }
                    }
                }
            }
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
            text = ":",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center,
        )
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


@Composable
private fun HadithReferenceEntity.label(): String = when (type) {
    HadithReferenceType.SUNNAHCOM_REFERENCE -> stringResource(R.string.ref_type_sunnahcom)
    HadithReferenceType.APP_REFERENCE -> stringResource(R.string.ref_type_app)
    HadithReferenceType.IN_BOOK_REFERENCE -> stringResource(R.string.ref_type_in_book)
    HadithReferenceType.ENGLISH_TRANSLATION -> stringResource(R.string.ref_type_english_translation)
    HadithReferenceType.ARABIC_ENGLISH_BOOK_REFERENCE -> stringResource(R.string.ref_type_arabic_english_book)
    HadithReferenceType.ARABIC_REFERENCE -> stringResource(R.string.ref_type_arabic)
    HadithReferenceType.US_MSA_REFERENCE -> stringResource(R.string.ref_type_us_msa)
}

@Composable
private fun HadithReferenceEntity.displayValue(): String {
    if (isDeprecated()) {
        return "$value ${stringResource(R.string.ref_deprecated)}"
    }
    return value
}

@Composable
private fun HadithReferenceEntity.isDeprecated(): Boolean {
    return type == HadithReferenceType.US_MSA_REFERENCE
}
