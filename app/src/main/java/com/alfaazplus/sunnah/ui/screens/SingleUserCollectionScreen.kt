package com.alfaazplus.sunnah.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenu
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheetMenuItem
import com.alfaazplus.sunnah.ui.components.library.CreateUpdateCollectionSheet
import com.alfaazplus.sunnah.ui.models.userdata.UserCollectionItemNormalized
import com.alfaazplus.sunnah.ui.utils.formatDateTimeShort
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
private fun CollectionItemMenu(
    item: UserCollectionItemNormalized,
    isOpen: Boolean,
    onClose: () -> Unit,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    BottomSheetMenu(
        title = "${item.collectionName}: ${item.item.hadithNumber}",
        isOpen = isOpen,
        onDismiss = onClose,
        headerArrangement = Arrangement.Center,
    ) {
        BottomSheetMenuItem(
            text = stringResource(R.string.remove_from_collection),
            icon = R.drawable.ic_delete,
        ) {
            scope.launch {
                viewModel.repo.removeItemFromUserCollection(
                    item.item.id,
                    item.item.hadithCollectionId,
                    item.item.hadithBookId,
                    item.item.hadithNumber,
                )
            }
        }
    }
}

@Composable
private fun CollectionItemCard(
    item: UserCollectionItemNormalized,
    onClick: (UserCollectionItemNormalized) -> Unit,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
        onClick = { onClick(item) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row {
                Card(
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        "${item.collectionName}: ${item.item.hadithNumber}",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Box(modifier = Modifier.weight(1f)) {}
                IconButton(
                    modifier = Modifier
                        .padding(0.dp)
                        .size(32.dp),
                    onClick = {
                        isMenuOpen = true
                    },
                ) {
                    Icon(
                        modifier = Modifier.padding(6.dp),
                        painter = painterResource(R.drawable.ic_ellipsis_vertical),
                        contentDescription = stringResource(R.string.desc_hadith_options),
                    )
                }
            }

            Text(
                text = item.translationText, style = MaterialTheme.typography.bodyMedium, maxLines = 8, overflow = TextOverflow.Ellipsis
            )
        }
    }

    CollectionItemMenu(
        item = item,
        isOpen = isMenuOpen,
        onClose = { isMenuOpen = false },
    )
}

@Composable
private fun CollectionItems(
    collectionItems: List<UserCollectionItemNormalized>,
    header: @Composable () -> Unit,
) {
    val navController = LocalNavHostController.current


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { header() }
        items(collectionItems.size) { index ->
            CollectionItemCard(collectionItems[index]) { item ->
                navController.navigate(
                    Routes.READER.args(
                        item.item.hadithCollectionId,
                        item.item.hadithBookId,
                        item.item.hadithNumber,
                    )
                )
            }
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    userCollection: UserCollection,
    bgColor: Color,
    textColor: Color,
    viewModel: UserDataViewModel = hiltViewModel(),
) {

    LaunchedEffect(userCollection.id) {
        viewModel.loadCollectionItems(userCollection.id)
    }

    val collectionItems by viewModel.collectionItems.collectAsState()

    val gradientColors = listOf(
        bgColor,
        bgColor,
        MaterialTheme.colorScheme.background,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (collectionItems.isEmpty()) 0.2f else 0.4f)
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                ),
        )

        if (collectionItems.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_library),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                )

                Text(
                    text = stringResource(R.string.no_collection_items),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            CollectionItems(
                header = {
                    if (!userCollection.description.isNullOrBlank()) {
                        Text(
                            text = userCollection.description,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }

                    Text(
                        formatDateTimeShort(userCollection.updatedAt),
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.alpha(0.8f),
                        fontStyle = FontStyle.Italic,
                    )
                },
                collectionItems = collectionItems,
            )
        }
    }
}

@Composable
fun SingleUserCollectionScreen(
    userCollectionId: Long,
    viewModel: UserDataViewModel = hiltViewModel(),
) {
    var showDeleteAlert by remember { mutableStateOf(false) }
    var showUpdateCollectionSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val view = LocalView.current
    val navController = LocalNavHostController.current

    val scope = rememberCoroutineScope()

    val uCollection by viewModel.repo
        .observeUserCollectionById(userCollectionId)
        .collectAsState(null)

    val userCollection = uCollection ?: return

    val bgColor = userCollection.color?.let { Color(it.toColorInt()) } ?: Color.DarkGray
    val textColor = if (bgColor.luminance() < 0.6f) Color.White else Color.Black

    DisposableEffect(textColor) {
        val isDarkBg = textColor == Color.White

        val insetsController = WindowCompat.getInsetsController((view.context as Activity).window, view)
        val orgLightStatusBar = insetsController.isAppearanceLightStatusBars

        insetsController.isAppearanceLightStatusBars = !isDarkBg

        onDispose {
            insetsController.isAppearanceLightStatusBars = orgLightStatusBar
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = userCollection.name,
                bgColor = bgColor,
                color = textColor,
                actions = {
                    IconButton(onClick = { showDeleteAlert = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    IconButton(onClick = { showUpdateCollectionSheet = true }) {
                        Icon(
                            painter = painterResource(R.drawable.pencil_line),
                            tint = textColor,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Content(
            paddingValues,
            userCollection,
            bgColor,
            textColor,
        )
    }

    CreateUpdateCollectionSheet(
        showUpdateCollectionSheet,
        onClose = { showUpdateCollectionSheet = false },
        collectionToUpdate = userCollection,
    )

    AlertDialog(
        isOpen = showDeleteAlert,
        onClose = { showDeleteAlert = false },
        title = stringResource(R.string.delete_collection),
        cancelText = stringResource(R.string.cancel),
        confirmText = stringResource(R.string.delete),
        confirmColors = MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError,
        onConfirm = {
            navController.popBackStack()

            scope.launch {
                viewModel.repo.deleteUserCollection(userCollectionId)

                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(context, R.string.msg_collection_deleted, Toast.LENGTH_LONG)
                        .show()
                }
            }
        },
        content = {
            Text(
                text = stringResource(R.string.msg_delete_user_collection),
            )
        },
    )
}