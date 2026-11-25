package com.kevinfreyap.jetspending.ui.screen.notification.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimeInputDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ReminderTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    var showDial by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ReminderTimePickerDialogContent(
            showDial = showDial,
            timePickerState = timePickerState,
            onDismiss = onDismiss,
            onShowDial = { showDial = !showDial },
            onConfirm = onConfirm
        )
    }
}

@Composable
fun ReminderTimePickerDialogContent(
    showDial: Boolean,
    timePickerState: TimePickerState,
    onDismiss: () -> Unit,
    onShowDial: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .widthIn(max = 360.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.dialog_title_select_reminder_time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.primaryTextColor
                )

                IconButton(
                    onClick = onShowDial,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (showDial) R.drawable.ic_keyboard_24
                            else R.drawable.ic_schedule_24
                        ),
                        contentDescription = stringResource(
                            if (showDial) R.string.cd_switch_to_text_input
                            else R.string.cd_switch_to_clock_dial
                        ),
                        tint = Theme.custom.secondaryTextColor
                    )
                }
            }

            // Dial or Keyboard Time Input
            if (showDial) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.surfaceContainer,
                        selectorColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,

                        clockDialContentColor = Theme.custom.secondaryTextColor,
                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,

                        timeSelectorSelectedContainerColor = Theme.custom.highlightContainer,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,

                        timeSelectorContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    )
                )
            } else {
                TimeInput(
                    state = timePickerState,
                    colors = TimeInputDefaults.colors(
                        timeTextFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                            focusedContainerColor = Theme.custom.highlightContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,

                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = Theme.custom.secondaryTextColor,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    ),
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor
                    )
                }

                TextButton(
                    onClick = {
                        onConfirm(timePickerState.hour, timePickerState.minute)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.btn_save),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ReminderTimePickerPreview() {
    JetSpendingTheme {
        val timePickerState = rememberTimePickerState(
            initialHour = 20,
            initialMinute = 0,
            is24Hour = true
        )

        ReminderTimePickerDialogContent(
            showDial = false,
            timePickerState = timePickerState,
            onDismiss = {  },
            onShowDial = {  },
            onConfirm = { _, _ -> }
        )
    }
}