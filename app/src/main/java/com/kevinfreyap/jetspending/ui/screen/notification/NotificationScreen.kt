package com.kevinfreyap.jetspending.ui.screen.notification

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewCustomTimeInput
import com.kevinfreyap.jetspending.ui.components.ViewPreferenceRow
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.state.NotificationAction
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val notificationState by viewModel.notificationState.collectAsState()

    var pendingNotificationType by rememberSaveable { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                when(pendingNotificationType) {
                    "DAILY" -> viewModel.onDailySwitch(true)
                    "MONTHLY" -> viewModel.onMonthlySwitch(true)
                }
            }

            pendingNotificationType = null
        }
    )

    NotificationContent(
        onBackClick = onBackClick,
        notificationState = notificationState,
        notificationAction = object : NotificationAction {
            override fun onSwitchDailyNotification(isChecked: Boolean) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            viewModel.onDailySwitch(true)
                        } else {
                            pendingNotificationType = "DAILY"
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        viewModel.onDailySwitch(true)
                    }
                } else {
                    viewModel.onDailySwitch(false)
                }
            }

            override fun onSwitchMonthlyNotification(isChecked: Boolean) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            viewModel.onMonthlySwitch(true)
                        } else {
                            pendingNotificationType = "MONTHLY"
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        viewModel.onMonthlySwitch(true)
                    }
                } else {
                    viewModel.onMonthlySwitch(false)
                }
            }

            override fun onTimeChange(time: String) {
                viewModel.onUpdateTime(time)
            }

        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationContent(
    onBackClick: () -> Unit,
    notificationState: NotificationPreferences?,
    notificationAction: NotificationAction,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    if (notificationState == null) {
        return
    }

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.notifications),
                onBackClick = onBackClick,
                onSelectCurrency = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    top = 32.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.set_notification),
                style = MaterialTheme.typography.headlineSmall,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(4.dp)
            )

            Text(
                text = stringResource(R.string.description_notification_settings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            ViewPreferenceRow(
                title = stringResource(R.string.monthly_summary)
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Switch(
                        checked = notificationState.isMonthlyEnabled,
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = Theme.custom.hintColor,
                            uncheckedTrackColor = Theme.custom.nestedCardColor,
                            uncheckedThumbColor = Theme.custom.iconColor
                        ),
                        onCheckedChange = { isChecked ->
                            notificationAction.onSwitchMonthlyNotification(isChecked)
                        },
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewPreferenceRow(
                title = stringResource(R.string.daily_reminder),
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Switch(
                        checked = notificationState.isDailyEnabled,
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = Theme.custom.hintColor,
                            uncheckedTrackColor = Theme.custom.nestedCardColor,
                            uncheckedThumbColor = Theme.custom.iconColor
                        ),
                        onCheckedChange = { isChecked ->
                            focusManager.clearFocus()
                            notificationAction.onSwitchDailyNotification(isChecked)
                        },
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Theme.custom.cardColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (notificationState.isDailyEnabled) 1f else 0.38f)
                    ) {
                        Text(
                            text = stringResource(R.string.daily_reminder_time),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Theme.custom.textColor,
                            modifier = Modifier
                                .align(
                                    Alignment.Start
                                )
                                .padding(
                                    bottom = 16.dp
                                )
                        )

                        ViewCustomTimeInput(
                            initialHour = notificationState.reminderHour,
                            initialMinute = notificationState.reminderMinute,
                            onTimeChange = notificationAction::onTimeChange
                        )

                    }

                    if (!notificationState.isDailyEnabled) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {}
                                )
                        )
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NotificationPreview() {
    JetSpendingTheme {
        NotificationContent(
            onBackClick = {},
            notificationState = NotificationPreferences(
                isDailyEnabled = false,
                isMonthlyEnabled = false
            ),
            notificationAction = object : NotificationAction{
                override fun onSwitchDailyNotification(isChecked: Boolean) {

                }

                override fun onSwitchMonthlyNotification(isChecked: Boolean) {

                }

                override fun onTimeChange(time: String) {

                }
            }
        )
    }
}