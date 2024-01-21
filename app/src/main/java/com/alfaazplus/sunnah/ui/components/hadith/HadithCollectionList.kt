package com.alfaazplus.sunnah.ui.components.hadith

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.BorderedCard
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.CollectionListViewModel


@Composable
private fun HadithCollectionItem(
    cwi: CollectionWithInfo,
    onClick: () -> Unit
) {
    BorderedCard(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CollectionIcon(collectionId = cwi.collection.id, height = 64.dp)
            Text(
                text = cwi.info?.name ?: "",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        }

        // Download icon at bottom right
        if (cwi.isDownloaded == false) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = 13.dp
                                .toPx()
                                .toInt(),
                            y = 13.dp
                                .toPx()
                                .toInt()
                        )
                    }
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                    .background(MaterialTheme.colorScheme.primary.alpha(0.2f))
                    .padding(
                        start = 6.dp,
                        top = 6.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    )
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    alpha = 0.8f,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun HadithCollectionList(
    modifier: Modifier,
    onCollectionClick: (collectionId: Int) -> Unit,
    vm: CollectionListViewModel = hiltViewModel()
) {
    val navController = LocalNavHostController.current

    LaunchedEffect(Unit) { vm.loadCollections() }

    val collections = vm.collections

    LazyVerticalGrid(
        columns = GridCells.Fixed(integerResource(R.integer.collection_grid_columns)),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp),
        modifier = modifier
    ) {
        items(collections.size) {
            val cwi = collections[it]
            HadithCollectionItem(cwi) {
                if (cwi.isDownloaded == true) {
                    onCollectionClick(cwi.collection.id)
                } else {
                    navController.navigate(Routes.SETTINGS_MANAGE_COLLECTIONS)
                }
            }
        }
    }
}