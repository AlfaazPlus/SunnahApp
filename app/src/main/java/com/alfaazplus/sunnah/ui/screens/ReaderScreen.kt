package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.alfaazplus.sunnah.ui.components.reader.LocalHadithActions
import com.alfaazplus.sunnah.ui.components.reader.LocalReader
import com.alfaazplus.sunnah.ui.components.reader.ReaderAppBar
import com.alfaazplus.sunnah.ui.components.reader.ReaderLayout
import com.alfaazplus.sunnah.ui.components.reader.ReaderNavigator
import com.alfaazplus.sunnah.ui.components.reader.ReaderProvider
import com.alfaazplus.sunnah.ui.components.reader.rememberAppBarDimensions
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.text.ComposeUiConfig
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel

class ReaderScaffoldController {
    var bottomBar by mutableStateOf<(@Composable () -> Unit)?>(null)
}

val LocalReaderScaffoldController = compositionLocalOf<ReaderScaffoldController> {
    error("LocalReaderBottomBarController not provided")
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    bookId: String,
    hadithId: String? = null,
    readerVm: ReaderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val colors by rememberUpdatedState(colorScheme)

    val showTwoPane = currentWindowAdaptiveInfo().windowSizeClass.isAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
        WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND,
    )

    LaunchedEffect(bookId, hadithId) {
        readerVm.onArguments(bookId, hadithId)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                readerVm.saveReadHistory()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ReaderProvider {
        CompositionLocalProvider(
            LocalReaderScaffoldController provides ReaderScaffoldController()
        ) {
            val reader = LocalReader.current
            val hadithActions = LocalHadithActions.current

            LaunchedEffect(lifecycleOwner, colors) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    readerVm.observeChanges(
                        uiConfig = ComposeUiConfig(
                            context = context,
                            colors = colors,
                        ),
                        hadithActions,
                    )
                }
            }

            val appBarDims = rememberAppBarDimensions(showTwoPane)
            val density = LocalDensity.current

            val readerTopBarState = rememberTopAppBarState(
                initialHeightOffsetLimit = with(density) { -appBarDims.expandedHeight.toPx() },
            )
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(readerTopBarState)

            val scaffoldController = LocalReaderScaffoldController.current

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    containerColor = reader.containerColor,
                    contentColor = reader.contentColor,
                    topBar = {
                        ReaderAppBar(
                            readerVm = readerVm,
                            isWideScreen = showTwoPane,
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    bottomBar = {
                        scaffoldController.bottomBar?.invoke()
                    },
                ) { padding ->
                    val contentModifier = Modifier.padding(padding)

                    if (showTwoPane) {
                        Row(
                            modifier = contentModifier.fillMaxSize(),
                        ) {
                            ReaderWideSidebar(
                                readerVm = readerVm,
                            )

                            VerticalDivider(color = colors.outlineVariant.alpha(0.6f))

                            ReaderLayout(
                                readerVm = readerVm,
                                nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                            )
                        }
                    } else {
                        Box(contentModifier) {
                            ReaderLayout(
                                readerVm = readerVm,
                                nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderWideSidebar(
    readerVm: ReaderViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(360.dp)
            .background(colorScheme.surfaceContainerLow),
    ) {
        ReaderNavigator(
            readerVm = readerVm,
            isInModal = false,
            onClose = {},
        )
    }
}
