package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.state.TransactionFilterAction
import com.kevinfreyap.jetspending.ui.state.TransactionFilterState
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun BottomSheetDateRange(
    transactionFilterState: TransactionFilterState,
    transactionFilterAction: TransactionFilterAction,
    modifier: Modifier = Modifier
) {
    val currentDate = LocalDate.now()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 16.dp
            )
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            IconButton(
                onClick = {
                    transactionFilterAction.onNavigateToFilter()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.back),
                    tint = Theme.custom.iconColor
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )

            Text(
                text = stringResource(R.string.date_range),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
            )

            TextButton(
                onClick = {
                    transactionFilterAction.onResetSelectedDate()
                },
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    textDecoration = TextDecoration.Underline,
                    color = Grey500
                )
            }
        }

        Text(
            text = stringResource(R.string.description_date_range),
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.custom.textColor,
            modifier = Modifier
                .padding(
                    horizontal = 16.dp
                )
        )

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp
                )
        ) {
            // From Date
            ViewDatePickerField(
                value = transactionFilterState.fromDateDisplay,
                rawValue = transactionFilterState.fromDateRaw,
                placeholder = stringResource(R.string.from),
                earliestYear = transactionFilterState.earliestTransactionYear,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val dayToCheck = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        val startYear = LocalDate.of(transactionFilterState.earliestTransactionYear, 1, 1)

                        return !dayToCheck.isBefore(startYear) && !dayToCheck.isAfter(currentDate)
                    }
                },
                onDateSelected = transactionFilterAction::onFromDateSelected,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
            // To Date
            ViewDatePickerField(
                value = transactionFilterState.toDateDisplay,
                rawValue = transactionFilterState.toDateRaw,
                placeholder = stringResource(R.string.to),
                earliestYear = transactionFilterState.earliestTransactionYear,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val dayToCheck = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        val fromDate = transactionFilterState.fromDateRaw
                        val fromDateLocalDate = if (fromDate?.toEpochMilli() != null){
                            Instant.ofEpochMilli(fromDate.toEpochMilli())
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                        } else {
                            currentDate
                        }

                        return dayToCheck.isAfter(fromDateLocalDate) && !dayToCheck.isAfter(currentDate)
                    }
                },
                isEnable = transactionFilterState.fromDateRaw?.let { startInstant ->
                    startInstant.atZone(ZoneOffset.UTC).toLocalDate() != currentDate
                } ?: false,
                onDateSelected = transactionFilterAction::onToDateSelected,
                modifier = Modifier
                    .weight(1f)
            )
        }

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Button(
            onClick = { transactionFilterAction.onSetSelectedDate() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.set_date),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun BottomSheetDateRangePreview() {
    JetSpendingTheme {
        BottomSheetDateRange(
            transactionFilterState = TransactionFilterState(
                earliestTransactionYear = 2020,
                fromDateRaw = null,
                fromDateDisplay = ""
            ),
            transactionFilterAction =
                object : TransactionFilterAction {
                    override fun onFilterOptionClicked(option: TimeFilterOption) {}

                    override fun onFromDateSelected(millis: Long?) {}

                    override fun onToDateSelected(millis: Long?) {}

                    override fun onSetSelectedDate() {}

                    override fun onResetSelectedDate() {}

                    override fun onNavigateToFilter() {}

                    override fun onFromAmountChanged(amount: String) {}

                    override fun onToAmountChanged(amount: String) {}

                    override fun onTypeChange(type: TransactionType) {}

                    override fun onCategorySelected(categoryId: String) {}

                    override fun onFromPositiveBtnClicked() {}

                    override fun onToPositiveBtnClicked() {}

                    override fun onApplyFilter() {}

                    override fun onResetFilter() {}
                }
        )
    }
}