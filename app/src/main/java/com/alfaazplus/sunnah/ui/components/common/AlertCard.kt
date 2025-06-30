package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.theme.alpha

sealed class AlertType {
    data object Info : AlertType()
    data object Error : AlertType()
}

@Composable
private fun getBackgroundColorForAlertType(type: AlertType): Color {
    return when (type) {
        AlertType.Info -> MaterialTheme.colorScheme.surface
        AlertType.Error -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun getBorderColorForAlertType(type: AlertType): Color {
    return when (type) {
        AlertType.Info -> MaterialTheme.colorScheme.onSurface
        AlertType.Error -> MaterialTheme.colorScheme.onError
    }
}

@Composable
fun AlertCard(
    type: AlertType = AlertType.Info,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.large,
                spotColor = Color.Black.alpha(0.3f),
            )
            .clip(MaterialTheme.shapes.large)
            .background(
                getBackgroundColorForAlertType(type),
                MaterialTheme.shapes.large,
            )
            .border(1.dp, getBorderColorForAlertType(type).copy(alpha = 0.1f), MaterialTheme.shapes.large)
            .padding(contentPadding),
    ) {
        content()
    }
}