package com.kevinfreyap.jetspending.ui.screen.notification

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.NotificationAction
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewTopBar
import com.kevinfreyap.jetspending.ui.navigation.NotificationNavigation
import com.kevinfreyap.jetspending.ui.screen.notification.components.ReminderTime
import com.kevinfreyap.jetspending.ui.screen.notification.components.ReminderTimePickerDialog
import com.kevinfreyap.jetspending.ui.screen.settings.components.SettingsItem
import com.kevinfreyap.jetspending.ui.screen.settings.components.SettingsSection
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemType
import com.kevinfreyap.jetspending.ui.state.NotificationState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    onNavigate: (NotificationNavigation) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    var pendingAction by remember { mutableStateOf<NotificationAction?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingAction?.let { action ->
                viewModel.onAction(action)
                pendingAction = null
            }
        } else {
            Toast.makeText(
                context,
                R.string.error_notification_permission_denied,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val handleActionWithPermission: (NotificationAction) -> Unit = { action ->
        if (hasNotificationPermission(context)) {
            viewModel.onAction(action)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingAction = action
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            openSystemNotificationSettings(context)
        }
    }

    NotificationContent(
        state = state,
        onAction = handleActionWithPermission,
        onNavigate = onNavigate,
        onOpenDeviceSettings = { openSystemNotificationSettings(context) },
        modifier = modifier
    )
}

@Composable
fun NotificationContent(
    state: NotificationState,
    onAction: (NotificationAction) -> Unit,
    onNavigate: (NotificationNavigation) -> Unit,
    onOpenDeviceSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.notifications),
                onBackClick = {
                    onNavigate(
                        NotificationNavigation.NavigateUp
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 12.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_notification_screen_header),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Theme.custom.primaryTextColor
                )

                Text(
                    text = stringResource(R.string.settings_notification_screen_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp),
            ) {
                SettingsItem(
                    state = SettingsItemState(
                        type = SettingsItemType.MASTER_NOTIFICATION,
                        iconRes = R.drawable.ic_notifications_24,
                        name = R.string.settings_master_notification,
                        description = R.string.description_settings_master_notification,
                        isChecked = state.isMasterNotificationEnabled
                    ),
                    onClick = {
                        onAction(
                            NotificationAction.OnToggleMasterNotification
                        )
                    },
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    ),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsSection(
                    header = R.string.settings_notification_type,
                    settings = listOf(
                        SettingsItemState(
                            type = SettingsItemType.WEEKLY_NOTIFICATION,
                            iconRes = R.drawable.ic_calendar_month_24,
                            name = R.string.settings_notification_weekly_summary,
                            description = R.string.description_settings_weekly_summary,
                            isChecked = state.isMasterNotificationEnabled && state.isWeeklyNotificationEnabled
                        ),
                        SettingsItemState(
                            type = SettingsItemType.DAILY_NOTIFICATION,
                            iconRes = R.drawable.ic_info_24,
                            name = R.string.settings_notification_daily_reminder,
                            description = R.string.description_settings_weekly_summary,
                            isChecked = state.isMasterNotificationEnabled && state.isDailyNotificationEnabled
                        ),
                    ),
                    onItemClick = { settingsItem ->
                        when(settingsItem) {
                            SettingsItemType.DAILY_NOTIFICATION -> {
                                onAction(
                                    NotificationAction.OnToggleDailyNotification
                                )
                            }
                            SettingsItemType.WEEKLY_NOTIFICATION -> {
                                onAction(
                                    NotificationAction.OnToggleWeeklyNotification
                                )
                            }
                            else -> Unit
                        }
                    },
                    modifier = Modifier
                        .alpha(
                            if (state.isMasterNotificationEnabled) 1f else 0.5f
                        )
                )

                AnimatedVisibility(
                    visible = state.isDailyNotificationEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ReminderTime(
                        hour = state.reminderHour,
                        minute = state.reminderMinute,
                        enabled = state.isDailyNotificationEnabled,
                        onClick = {
                            onAction(
                                NotificationAction.OnShowTimeDialog(
                                    isVisible = true
                                )
                            )
                        },
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.hint_notification),
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor
                )

                ViewPrimaryButton(
                    text = stringResource(R.string.btn_device_settings),
                    onClick = onOpenDeviceSettings,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }

    if (state.isReminderDialogVisible) {
        ReminderTimePickerDialog(
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            onDismiss = {
                onAction(
                    NotificationAction.OnShowTimeDialog(
                        isVisible = false
                    )
                )
            },
            onConfirm = { hour, minute ->
                onAction(
                    NotificationAction.OnConfirmTime(
                        hour = hour,
                        minute = minute
                    )
                )
            }
        )
    }
}

private fun hasNotificationPermission(context: Context): Boolean {
    val areNotificationEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

    return areNotificationEnabled && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED)
}

private fun openSystemNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
    context.startActivity(intent)
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NotificationPreview() {
    JetSpendingTheme {
        NotificationContent(
            state = NotificationState(
                isMasterNotificationEnabled = true
            ),
            onAction = {},
            onNavigate = {},
            onOpenDeviceSettings = {}
        )
    }
}