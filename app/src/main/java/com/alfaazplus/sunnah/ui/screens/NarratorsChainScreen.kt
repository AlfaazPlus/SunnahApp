package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.helpers.ScholarsHelper
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.NarratorsViewModel

@Composable
private fun NarratorCard(
    narrator: Scholar,
) {
    val navController = LocalNavHostController.current
    val rankColor = ScholarsHelper.getScholarRankColor(narrator.rank)

    Card(
        colors = CardDefaults.outlinedCardColors(
            containerColor = rankColor.alpha(0.15f),

        ),
        border = BorderStroke(
            color = rankColor.alpha(0.5f),
            width = 1.dp
        ),
        onClick = {
            navController.navigate(route = Routes.SCHOLAR_INFO.arg(narrator.id))
        },
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = "${narrator.shortName}\n${narrator.arabic}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.alpha(0.8f),
                text = ScholarsHelper.getScholarRankName(narrator.rank) ?: "",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun NarratorsList(narrators: List<Scholar>) {
    Logger.d(narrators.map { "${it.shortName} (${it.id})" })
    val totalNarrators = narrators.size
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = 50.dp,
            bottom = 50.dp,
        ),
    ) {
        items(
            count = narrators.size
        ) { i ->
            NarratorCard(narrators[i])

            if (i < totalNarrators - 1) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_top),
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(180f)
                        .padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
fun NarratorsChainScreen(
    hadithUrn: Int,
    vm: NarratorsViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        vm.loadNarrators(hadithUrn)
    }

    val narrators = vm.narrators

    Scaffold(
        topBar = { AppBar(title = stringResource(R.string.narrators_chain)) },
    ) { paddingValues ->
        Box(

            modifier = Modifier.padding(paddingValues)
        ) {
            NarratorsList(narrators)
        }
    }
}