package com.alfaazplus.sunnah.ui.components.library

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.launch

@Composable
private fun Content(
    onCancel: () -> Unit,
    onCreate: (UserCollection) -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var value by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val bgColor = MaterialTheme.colorScheme.background
    val scope = rememberCoroutineScope()

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
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = {
                description = it
            },
            maxLines = 5,
            minLines = 5,
            label = { Text("Description") },
            placeholder = { Text("Enter collection description") },
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
            modifier = Modifier.weight(1f),
            onClick = {
                scope.launch {
                    val newCollection = viewModel.repo.addUserCollection(
                        UserCollection(
                            name = value,
                            description = if (description.isBlank()) null else description.trim(),
                        )
                    )

                    onCreate(newCollection)
                }
            },
            enabled = value.isNotBlank(),
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
    onCreate: (UserCollection) -> Unit,
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