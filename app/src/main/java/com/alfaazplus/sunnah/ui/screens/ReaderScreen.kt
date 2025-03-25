package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.components.reader.HorizontalReader
import com.alfaazplus.sunnah.ui.components.reader.VerticalReader
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel


@Composable
fun ReaderScreen(
    collectionId: Int = 1,
    bookId: Int = 1,
    hadithNumber: String? = null,
    vm: ReaderViewModel = hiltViewModel(),
) {
    vm.primaryColor = MaterialTheme.colorScheme.primary
    vm.onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    LaunchedEffect(collectionId, bookId) {
        if (!vm.initialized) {
            vm.initialHadithNumber = Pair(hadithNumber, false)
            vm.collectionId = collectionId
            vm.bookId.value = bookId
            vm.loadEssentials()
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

    val layoutOption = vm.hadithLayout

    if (layoutOption == ReaderUtils.HADITH_LAYOUT_VERTICAL) {
        VerticalReader(vm)
    } else {
        HorizontalReader(vm)
    }
}