package com.kevinfreyap.jetspending.ui.components.v2

import android.content.res.Configuration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant
import java.time.ZoneOffset

@Composable
fun ViewCustomDateDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    minDateMillis: Long? = null,
    maxDateMillis: Long = System.currentTimeMillis(),
    yearRange: IntRange = DatePickerDefaults.YearRange
) {
    val selectableDates = remember(minDateMillis, maxDateMillis) {
        val minDate = minDateMillis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
        }
        val maxDate = Instant.ofEpochMilli(maxDateMillis).atZone(ZoneOffset.UTC).toLocalDate()
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                val afterMin = minDate == null || !date.isBefore(minDate)
                val beforeMax = !date.isAfter(maxDate)
                return afterMin && beforeMax
            }

            override fun isSelectableYear(year: Int): Boolean {
                val afterMinYear = minDate == null || year >= minDate.year
                val beforeMaxYear = year <= maxDate.year
                return afterMinYear && beforeMaxYear
            }
        }
    }

    val defaultInitialMillis = remember(initialDateMillis, minDateMillis, maxDateMillis) {
        (initialDateMillis ?: System.currentTimeMillis()).coerceIn(
            minimumValue = minDateMillis ?: Long.MIN_VALUE,
            maximumValue = maxDateMillis
        )
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = defaultInitialMillis,
        selectableDates = selectableDates,
        yearRange = yearRange
    )

    ViewCustomDateDialog(
        datePickerState = datePickerState,
        onDateSelected = { selectedMillis ->
            selectedMillis?.let { onDateSelected(it) }
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
fun ViewCustomDateDialog(
    datePickerState: DatePickerState,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        weekdayContentColor = Theme.custom.primaryTextColor,
        navigationContentColor = Theme.custom.primaryTextColor,
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,

        todayDateBorderColor = Color.Transparent,
        todayContentColor = Theme.custom.primaryTextColor,

        dayContentColor = Theme.custom.primaryTextColor,
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
        },
        modifier = modifier
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