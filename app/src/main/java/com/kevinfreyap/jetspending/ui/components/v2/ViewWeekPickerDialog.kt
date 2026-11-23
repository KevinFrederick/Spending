package com.kevinfreyap.jetspending.ui.components.v2

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

@Composable
fun ViewWeekPickerDialog(
    initialDate: LocalDate,
    onWeekSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    maxDate: LocalDate = LocalDate.now()
) {
    val initialMonday = initialDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val initialSunday = initialMonday.plusDays(6)

    val initialStartMillis = initialMonday.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val initialEndMillis = initialSunday.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val selectableDates = remember(maxDate) {
        val maxDateUtc = maxDate.atStartOfDay(ZoneOffset.UTC).toLocalDate()
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                return !date.isAfter(maxDateUtc)
            }
        }
    }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartMillis,
        initialSelectedEndDateMillis = initialEndMillis,
        selectableDates = selectableDates
    )

    val selectedStart = dateRangePickerState.selectedStartDateMillis
    val selectedEnd = dateRangePickerState.selectedEndDateMillis

    LaunchedEffect(selectedStart, selectedEnd) {
        val activeMillis = selectedStart ?: selectedEnd
        if (activeMillis != null) {
            val clickedDate = Instant.ofEpochMilli(activeMillis)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            val monday = clickedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sunday = monday.plusDays(6)

            val mondayMillis = monday.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val sundayMillis = sunday.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

            if (dateRangePickerState.selectedStartDateMillis != mondayMillis ||
                dateRangePickerState.selectedEndDateMillis != sundayMillis
            ) {
                dateRangePickerState.setSelection(
                    startDateMillis = mondayMillis,
                    endDateMillis = sundayMillis
                )
            }
        }
    }

    ViewWeekPickerContent(
        dateRangePickerState = dateRangePickerState,
        onConfirm = {
            val startMillis = dateRangePickerState.selectedStartDateMillis ?: initialStartMillis
            val mondayDate = Instant.ofEpochMilli(startMillis)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
            onWeekSelected(mondayDate)
            onDismiss()
        },
        onDismiss = onDismiss,
        onReset = onReset,
        modifier = modifier
    )
}

@Composable
fun ViewWeekPickerContent(
    dateRangePickerState: DateRangePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        weekdayContentColor = Theme.custom.primaryTextColor,
        navigationContentColor = Theme.custom.primaryTextColor,

        // Highlighted week range colors
        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        dayInSelectionRangeContentColor = Theme.custom.primaryTextColor,
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,

        todayDateBorderColor = Color.Transparent,
        todayContentColor = Theme.custom.primaryTextColor,
        dayContentColor = Theme.custom.primaryTextColor
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        confirmButton = {},
        dismissButton = {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        onReset()
                        onDismiss()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor.copy(0.6f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Theme.custom.secondaryTextColor
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    TextButton(
                        onClick = onConfirm,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        DateRangePicker(
            state = dateRangePickerState,
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
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun ViewWeekPickerDialogPreview() {
    val today = LocalDate.now()
    val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val sunday = monday.plusDays(6)

    val startMillis = monday.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val endMillis = sunday.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startMillis,
        initialSelectedEndDateMillis = endMillis
    )

    JetSpendingTheme {
        ViewWeekPickerContent(
            dateRangePickerState = dateRangePickerState,
            onConfirm = {},
            onDismiss = {},
            onReset = {}
        )
    }
}