package com.alfaazplus.sunnah.ui.components.library

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils

@Composable
private fun Content(
    onCancel: () -> Unit,
    onCreate: (Int) -> Unit,
) {
    var value by remember { mutableStateOf("") }

    val bgColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = {
                value = it
            },
            singleLine = true,
            label = { Text("Collection Name") },
            placeholder = { Text("Enter collection name") },
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor,
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Text("Cancel")
        }

        Button(
            modifier = Modifier.weight(1f), onClick = { // TODO: Handle collection creation logic here
                onCreate(0) // Replace with actual collection ID
                MessageUtils.showToast(context, "WIP", Toast.LENGTH_SHORT)
            }, enabled = value.isNotBlank()
        ) {
            Text("Create")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionSheet(
    isOpen: Boolean,
    onCancel: () -> Unit,
    onCreate: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(true) { sheetValue ->
        when (sheetValue) {
            SheetValue.Hidden -> false
            SheetValue.Expanded,
            SheetValue.PartiallyExpanded,
                -> true
        }
    }

    if (!isOpen) return

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false,
        ),
        dragHandle = {},
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Create Collection",
                style = MaterialTheme.typography.titleMedium,
            )

        }

        Content(
            onCancel = onCancel,
            onCreate = onCreate,
        )
    }
}