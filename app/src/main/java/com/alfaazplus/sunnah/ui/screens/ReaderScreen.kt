package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.reader.HorizontalReader
import com.alfaazplus.sunnah.ui.components.reader.VerticalReader
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch


@Composable
fun ReaderScreen(
    collectionId: Int = 1,
    bookId: Int = 1,
    hadithNumber: String? = null,
    vm: ReaderViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    vm.primaryColor = MaterialTheme.colorScheme.primary
    vm.onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    LaunchedEffect(collectionId, bookId) {
        if (!vm.initialized) {
            vm.initialHadithNumber = Pair(hadithNumber, false)
            vm.collectionId = collectionId
            vm.bookId.value = bookId

            coroutineScope.launch {
                vm.loadEssentials()
            }
        }
    }

    if (vm.cwi?.isFailure == true || vm.bwi?.isFailure == true) {
        return Box(
            contentAlignment = Alignment.Center
        ) {
            Text("Not found")
        }
    }

    val hadithList = vm.parsedHadithList

    if (vm.cwi == null || vm.bwi == null || !vm.initialized) {
        return Box(
            contentAlignment = Alignment.Center
        ) {
            Loader(size = 24.dp)
        }
    }

    if (hadithList.isEmpty()) {
        return Box(
            contentAlignment = Alignment.Center
        ) {
            Text("No Hadiths found")
        }
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                vm.saveReadHistory()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isHorizontal = vm.hadithLayout == ReaderUtils.HADITH_LAYOUT_HORIZONTAL

    if (isHorizontal) {
        HorizontalReader(vm)
    } else {
        VerticalReader(vm)
    }
}