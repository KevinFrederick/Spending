package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewCustomDateDialog(
    datePickerState: DatePickerState,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        weekdayContentColor = Theme.custom.textColor,
        navigationContentColor = Theme.custom.textColor,
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,

        todayDateBorderColor = Color.Transparent,
        todayContentColor = Theme.custom.textColor,

        dayContentColor = Theme.custom.textColor,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.confirm)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Grey500
                )
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = colors,
            title = null,
            headline = null,
            showModeToggle = false
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ViewCustomDateDialogPreview() {
    JetSpendingTheme {
        ViewCustomDateDialog(
            datePickerState = rememberDatePickerState(),
            onDateSelected = {},
            onDismiss = {}
        )
    }
}