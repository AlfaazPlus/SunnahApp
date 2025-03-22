package com.alfaazplus.sunnah.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    title: String,
    cancelText: String,
    confirmText: String,
    confirmColors: Pair<Color, Color>? = null,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (!isOpen) return

    BasicAlertDialog(
        onDismissRequest = onClose
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        colors = textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = onClose,
                    ) {
                        Text(text = cancelText)
                    }

                    TextButton(
                        modifier = Modifier.weight(1f),
                        colors = textButtonColors(
                            containerColor = confirmColors?.first ?: MaterialTheme.colorScheme.primary,
                            contentColor = confirmColors?.second ?: MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            onClose()
                            onConfirm()
                        },
                    ) {
                        Text(text = confirmText)
                    }
                }
            }
        }
    }
}