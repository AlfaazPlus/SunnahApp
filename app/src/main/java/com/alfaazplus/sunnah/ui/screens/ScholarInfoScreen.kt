package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.parseAsHtml
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.helpers.ScholarsHelper
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString
import com.alfaazplus.sunnah.ui.viewModels.ScholarInfoViewModel

@Composable
fun InformationTable(info: Map<String, String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        info.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        }
    }
}

@Composable
private fun Header(scholar: Scholar) {
    val rankColor = ScholarsHelper.getScholarRankColor(scholar.rank)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = rankColor.alpha(0.15f),
                contentColor = MaterialTheme.colorScheme.onBackground,
            ),
            border = BorderStroke(
                color = rankColor.alpha(0.5f),
                width = 1.dp,
            ),
        ) {
            Text(
                text = ScholarsHelper.getScholarRankName(scholar.rank) ?: "",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }

        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
            text = scholar.shortName ?: "",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = scholar.arabic ?: "",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = fontUthmani,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun BiographySection(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            text = content
                .replace("\n", "<br/>")
                .parseAsHtml()
                .toAnnotatedString(),
            style = TextStyle(fontSize = 16.sp),
        )

    }
}

@Composable
private fun ScholarInfo(scholar: Scholar) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 120.dp)
    ) {
        Header(scholar)
        if (!scholar.bio.isNullOrBlank()) {
            BiographySection(
                title = "Biography",
                content = scholar.bio,
            )
        }
        InformationTable(mapOf(
            "Full Name" to (scholar.fullName ?: "—"),
            "Kunya" to (scholar.kunya.takeIf { !it.isNullOrBlank() } ?: "—"),
            "Birth" to (scholar.birthDate
                .takeIf { !it.isNullOrBlank() }
                ?.apply { "$this (${scholar.birthPlace.takeIf { !it.isNullOrBlank() } ?: "—"})" } ?: "—"),
            "Death" to (scholar.deathDate
                .takeIf { !it.isNullOrBlank() }
                ?.apply { "$this (${scholar.deathPlace.takeIf { !it.isNullOrBlank() } ?: "—"})" } ?: "—"),
            "Parents" to "${scholar.fatherName.takeIf { !it.isNullOrBlank() } ?: "—"} / ${scholar.motherName.takeIf { !it.isNullOrBlank() } ?: "—"}",
            "Siblings" to (scholar.siblings.takeIf { !it.isNullOrBlank() } ?: "—"),
            "Spouses" to (scholar.spouses.takeIf { !it.isNullOrBlank() } ?: "—"),
            "Children" to (scholar.children.takeIf { !it.isNullOrBlank() } ?: "—"),
            "Places of stay" to (scholar.city.takeIf { !it.isNullOrBlank() } ?: "—"),
            "Interests" to ScholarsHelper.getInterestNames(scholar.interests),
            "Teachers" to (scholar.teachers.takeIf { !it.isNullOrBlank() } ?: "—"),
            "Students" to (scholar.students.takeIf { !it.isNullOrBlank() } ?: "—"),
        ))
    }
}


@Composable
fun ScholarInfoScreen(
    scholarId: Int,
    vm: ScholarInfoViewModel = hiltViewModel(),
) {
    val scholar by vm.scholar.collectAsState()

    LaunchedEffect(scholarId) {
        vm.setScholarId(scholarId)
    }

    Scaffold(
        topBar = { AppBar(title = stringResource(R.string.scholar_info)) },
    ) { paddingValues ->
        SelectionContainer {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                if (scholar != null) {
                    ScholarInfo(scholar!!)
                } else {
                    Loader(true)
                }
            }
        }
    }
}

