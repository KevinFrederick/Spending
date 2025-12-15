package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun ViewDatePickerField(
    value: String?,
    rawValue: Instant?,
    placeholder: String,
    earliestYear: Int,
    selectableDates: SelectableDates,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    isEnable: Boolean = true
) {
    val currentDate = LocalDate.now()
    var showCalendarDialog by remember { mutableStateOf(false) }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .alpha(if (isEnable) 1f else 0.53f)
            .background(Theme.custom.cardColor)
            .clickable(
                enabled = isEnable,
                onClick = {showCalendarDialog = true}
            )
            .padding(16.dp)
    ){
        Text(
            text = if (!value.isNullOrBlank()) value else placeholder,
            color = if (!value.isNullOrBlank()) Theme.custom.textColor else Theme.custom.hintColor,
            style = MaterialTheme.typography.bodyLarge
        )

        Icon(
            painter = painterResource(R.drawable.ic_calendar_month_24),
            tint = Theme.custom.hintColor,
            contentDescription = "Calendar Picker",
            modifier = Modifier
                .padding(end = 16.dp)
        )
    }

    if (showCalendarDialog) {
        ViewCustomDateDialog(
            datePickerState = rememberDatePickerState(
                initialSelectedDateMillis =rawValue?.toEpochMilli(),
                yearRange = IntRange(earliestYear, currentDate.year),
                selectableDates = selectableDates
            ),
            onDateSelected = onDateSelected,
            onDismiss = {
                showCalendarDialog = false
            }
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun ViewDatePickerFieldPreview() {
    JetSpendingTheme {
        ViewDatePickerField(
            value = "",
            placeholder = "From",
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val dayToCheck = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()

                    val startYear = LocalDate.of(2020, 1, 1)

                    return !dayToCheck.isBefore(startYear) && !dayToCheck.isAfter(LocalDate.now())
                }
            },
            onDateSelected = {},
            rawValue = Instant.now(),
            earliestYear = 2020,
        )
    }
}