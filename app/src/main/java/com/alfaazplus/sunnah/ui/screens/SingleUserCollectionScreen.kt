package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel

@Composable
private fun Content(
    paddingValues: PaddingValues,
    viewModel: UserDataViewModel = hiltViewModel(),
) {

}

@Composable
fun SingleUserCollectionScreen(
    userCollectionId: Int,
    userCollectionName: String,
) {
    Scaffold(
        topBar = { AppBar(title = userCollectionName) },
    ) { paddingValues ->
        Content(
            paddingValues = paddingValues,
        )
    }
}