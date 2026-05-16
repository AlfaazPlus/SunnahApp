package com.alfaazplus.sunnah.ui.components.reader.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.helpers.HadithGradeText
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet


@Composable
fun HadithGradeInfoSheet(
    gradeInfo: HadithGradeText?,
    onClose: () -> Unit,
) {

    BottomSheet(
        isOpen = gradeInfo != null,
        onDismiss = onClose,
    ) {
        if (gradeInfo != null) {
            Text(
                text = buildAnnotatedString {
                    gradeInfo.descriptions.forEach {
                        appendLine("- $it")
                    }
                },
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}
