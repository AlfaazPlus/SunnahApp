package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.models.HadithChapterUi
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.tightTextStyle
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.extension.bottomBorder

@Composable
fun HadithItemView(
    modifier: Modifier = Modifier,
    hadithUi: ReaderLayoutItem.HadithUI,
    isVertical: Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                if (!isVertical) Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp)
                else Modifier
            )
            .then(
                if (isVertical && hadithUi.showDivider) Modifier.bottomBorder(color = colorScheme.outline.alpha(0.5f))
                else Modifier
            )
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (hadithUi.chapterUi != null) {
            ChapterInfo(hadithUi.chapterUi)
        }

        HadithActionBar(hadithUi)

        SelectionContainer {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (!hadithUi.parsedTranslationText.isNullOrEmpty()) {
                    HadithText(
                        text = hadithUi.parsedTranslationText,
                        shouldTrim = isVertical,
                    )
                }

                if (!hadithUi.parsedArabicText.isNullOrEmpty()) {
                    HadithText(
                        text = hadithUi.parsedArabicText,
                        shouldTrim = isVertical,
                        maxLines = 15,
                    )
                }
            }
        }

        HadithGrade(hadithUi)
    }
}

@Composable
private fun HadithText(text: AnnotatedString, shouldTrim: Boolean, maxLines: Int = 15) {
    val reader = LocalReader.current
    val interactionSource = remember { MutableInteractionSource() }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }
    var trimEndOffset by remember(text) { mutableIntStateOf(-1) }
    val isTrimmed = shouldTrim && !expanded

    val isMeasured = trimEndOffset > 0
    val showFade = isTrimmed && isOverflowing && isMeasured

    val displayText = if (isTrimmed && isMeasured && trimEndOffset < text.length) {
        text.subSequence(0, trimEndOffset)
    } else {
        text
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { clip = false }
            .clickable(
                enabled = shouldTrim,
                interactionSource = interactionSource,
                indication = null,
            ) {
                val willCollapse = expanded
                expanded = !expanded

                if (willCollapse) {
                    trimEndOffset = -1
                }
            },
    ) {
        Text(
            text = displayText,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { clip = false },
            onTextLayout = { result ->
                // All these to prevent Urdu glyph clipping

                if (!isTrimmed) {
                    isOverflowing = false
                    trimEndOffset = -1
                    return@Text
                }

                if (trimEndOffset > 0) return@Text

                if (result.lineCount > maxLines) {
                    isOverflowing = true
                    trimEndOffset = result.getLineEnd(maxLines - 1, visibleEnd = true)
                } else {
                    isOverflowing = false
                    trimEndOffset = -1
                }
            },
        )

        if (showFade) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        val fadeHeight = size.height * 0.25f
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, reader.containerColor),
                                startY = size.height - fadeHeight,
                                endY = size.height,
                            ),
                        )
                    },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Icon(
                    painterResource(R.drawable.ic_chevron_down), contentDescription = null, tint = colorScheme.onBackground.alpha(0.5f)
                )
            }
        }
    }
}

@Composable
private fun ChapterInfo(chapterUi: HadithChapterUi) {
    var expanded by remember { mutableStateOf(false) }

    val hasIntro = chapterUi.intros.isNotEmpty()
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chapter_chevron",
    )

    val containerColor = colorScheme.surfaceContainerHigh
    val contentColor = colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(containerColor)
            .clickable { expanded = !expanded },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.chapter),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
            )

            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = stringResource(R.string.show_chapter),
                modifier = Modifier
                    .size(18.dp)
                    .rotate(chevronRotation)
                    .alpha(0.7f),
                tint = contentColor,
            )
        }


        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            SelectionContainer {
                Column {
                    HorizontalDivider(
                        color = colorScheme.outline.alpha(0.3f),
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        chapterUi.titles.forEach { text ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = text,
                                textAlign = TextAlign.Center,
                                color = contentColor,
                            )
                        }
                    }

                    if (hasIntro) {
                        HorizontalDivider(color = colorScheme.outline.alpha(0.3f))

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {

                            chapterUi.intros.forEach { text ->
                                Text(
                                    text = text,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = contentColor.alpha(0.8f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HadithActionBar(
    hadithUi: ReaderLayoutItem.HadithUI,
) {
    val isDarkTheme = ThemeUtils.observeDarkTheme()

    val actions = LocalHadithActions.current
    val bgColor = if (isDarkTheme) colorScheme.surfaceContainer else colorScheme.background
    val txtColor = if (isDarkTheme) colorScheme.onSurface else colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SimpleTooltip(
            text = stringResource(R.string.desc_hadith_options)
        ) {
            IconButton(
                modifier = Modifier
                    .padding(0.dp)
                    .size(32.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = bgColor,
                    contentColor = txtColor,
                ),
                onClick = {
                    actions.onHadithOption(hadithUi.hadithId)
                },
            ) {
                Icon(
                    modifier = Modifier.padding(6.dp),
                    painter = painterResource(R.drawable.ic_ellipsis_vertical),
                    contentDescription = stringResource(R.string.desc_hadith_options),
                )
            }
        }

        if (hadithUi.hasNarratorsChain) {
            SimpleTooltip(
                text = stringResource(R.string.desc_narrators_chain)
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(0.dp)
                        .size(32.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = bgColor,
                        contentColor = txtColor,
                    ),
                    onClick = {
                        actions.showNarratorsChain(hadithUi.hadithId)
                    },
                ) {
                    Icon(
                        modifier = Modifier.padding(6.dp),
                        painter = painterResource(R.drawable.ic_users),
                        contentDescription = stringResource(R.string.desc_narrators_chain),
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Card(
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = bgColor,
                contentColor = txtColor,
            ),
            onClick = { actions.onNumberReferenceRequest(hadithUi.hadithId) },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    hadithUi.visibleNumbering,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Icon(
                    modifier = Modifier.size(14.dp),
                    painter = painterResource(R.drawable.ic_chevron_down),
                    contentDescription = null,
                )
            }
        }
    }

}


@Composable
private fun HadithGrade(hadithUi: ReaderLayoutItem.HadithUI) {
    val gradeText = hadithUi.gradeText ?: return
    val actions = LocalHadithActions.current
    val hasDescriptions = gradeText.descriptions.isNotEmpty()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(enabled = hasDescriptions) {
                actions.showGradeInfo(gradeText)
            }
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            gradeText.label,
            style = MaterialTheme.typography.labelMedium.merge(tightTextStyle),
            color = gradeText.colors.first,
        )
        if (hasDescriptions) {
            Icon(
                painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = gradeText.colors.first,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}


@Composable
private fun Modifier.highlightHadithItem(show: Boolean): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (show) 0.3f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "",
    )

    return this.background(colorScheme.primary.alpha(alpha))
}
