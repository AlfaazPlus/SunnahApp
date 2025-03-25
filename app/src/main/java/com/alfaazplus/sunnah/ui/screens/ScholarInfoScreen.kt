package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar


@Composable
fun ScholarInfoScreen(
    scholarId: Int,
) {
    Scaffold(
        topBar = { AppBar(title = stringResource(R.string.scholar_info)) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text("WIP", style = MaterialTheme.typography.titleLarge)
        }
    }
}