package com.alfaazplus.sunnah.ui.components.settings

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.dialogs.AlertDialog
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.utils.workers.HadithOfTheDayScheduler
import com.alfaazplus.sunnah.ui.viewModels.HadithRepoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DailyReminderSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    repoViewModel: HadithRepoViewModel = hiltViewModel(),
) {
    val hotdEnabled = DataStoreManager.observe(booleanPreferencesKey(Keys.DAILY_REMINDER), false)
    var showPermissionDialog by remember { mutableStateOf<Pair<Boolean, Boolean>>(Pair(false, false)) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val items = listOf(
        Triple(true, R.string.enable, R.string.daily_reminder_desc),
        Triple(false, R.string.disable, null),
    )

    LaunchedEffect(permissionState) {
        if (permissionState != null && !permissionState.status.isGranted) {
            DataStoreManager.write(booleanPreferencesKey(Keys.DAILY_REMINDER), false)
            HadithOfTheDayScheduler.cancelDailyNotification(context)
        }
    }

    suspend fun validate(newStatus: Boolean): Boolean {
        if (newStatus == false) {
            return true
        }

        if (permissionState != null) {
            if (!permissionState.status.isGranted) {
                showPermissionDialog = Pair(true, !permissionState.status.shouldShowRationale)
                return false
            }
        }

        if (!repoViewModel.repo.isAnyCollectionDownloaded()) {
            MessageUtils.showToast(context, R.string.download_collection_first, Toast.LENGTH_LONG)
            return false
        }

        return true
    }


    BottomSheet(
        isOpen = isOpen,
        onDismiss = onClose,
        icon = R.drawable.bell,
        title = stringResource(R.string.daily_reminder),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            items.forEach { (key, title, desc) ->
                RadioItem(
                    title = title,
                    subtitle = desc,
                    selected = key == hotdEnabled,
                    onClick = {
                        coroutineScope.launch {
                            if (validate(key)) {
                                DataStoreManager.write(booleanPreferencesKey(Keys.DAILY_REMINDER), key)

                                if (key == true) {
                                    HadithOfTheDayScheduler.scheduleDailyNotification(context)
                                } else {
                                    HadithOfTheDayScheduler.cancelDailyNotification(context)
                                }
                            }
                        }

                        onClose()
                    },
                )
            }
        }
    }

    AlertDialog(
        isOpen = showPermissionDialog.first,
        onClose = { showPermissionDialog = showPermissionDialog.copy(false) },
        title = stringResource(R.string.notification_permission),
        cancelText = stringResource(R.string.cancel),
        confirmText = stringResource(R.string.allow),
        onConfirm = {
            permissionState?.let {
                if (showPermissionDialog.second == true) {
                    NavigationHelper.openAppSettings(context)
                } else {
                    it.launchPermissionRequest()
                }
            }
            showPermissionDialog = showPermissionDialog.copy(false)
        },
        content = {
            Text(
                text = stringResource(R.string.notification_permission_desc),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
    )
}