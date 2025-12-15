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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.FilterTimeOptionUI
import com.kevinfreyap.jetspending.ui.state.TransactionFilterAction
import com.kevinfreyap.jetspending.ui.state.TransactionFilterState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun BottomSheetFilter(
    transactionFilterState: TransactionFilterState,
    transactionFilterAction: TransactionFilterAction,
    uiState: UiState<Unit>,
    modifier: Modifier = Modifier
) {
    val amountError = if (uiState is UiState.ValidationErrors){
        uiState.errors[Field.TRANSACTION_AMOUNT]
    } else null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.transaction_filter),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleLarge,
            )

            TextButton(
                onClick = {
                    transactionFilterAction.onResetFilter()
                },
                contentPadding = PaddingValues(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    textDecoration = TextDecoration.Underline,
                    color = Grey500
                )
            }
        }

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        Text(
            text = stringResource(R.string.by_time),
            color = Grey500,
            style = MaterialTheme.typography.titleSmall
        )

        Column(
            modifier = Modifier
                .selectableGroup()
        ) {

            transactionFilterState.timeFilterOptions.forEach { timeOption ->

                ViewFilterTimeOption(
                    text = stringResource(timeOption.label),
                    isSelected = (timeOption.id == transactionFilterState.timeFilter),
                    onOptionClicked = {
                        transactionFilterAction.onFilterOptionClicked(timeOption.id)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Text(
            text = stringResource(R.string.by_amount),
            color = Grey500,
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        Row {
            // From 
            ViewAmountCard(
                onTransactionAmountClick = {},
                cardTitle = stringResource(R.string.from),
                transactionAmount = transactionFilterState.displayFromAmount,
                transactionAmountInput = transactionFilterState.fromAmountInput,
                onTransactionAmountChange = transactionFilterAction::onFromAmountChanged,
                isError = amountError != null,
                errorMessage = amountError?.let { stringResource(it) } ?: "",
                currencyCode = AppCurrency.IDR,
                onPositiveBtnBottomSheet = transactionFilterAction::onFromPositiveBtnClicked,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )

            // To
            ViewAmountCard(
                onTransactionAmountClick = {},
                cardTitle = stringResource(R.string.to),
                transactionAmount = transactionFilterState.displayToAmount,
                transactionAmountInput = transactionFilterState.toAmountInput,
                onTransactionAmountChange = transactionFilterAction::onToAmountChanged,
                isError = amountError != null,
                errorMessage = amountError?.let { stringResource(it) } ?: "",
                currencyCode = AppCurrency.IDR,
                onPositiveBtnBottomSheet = transactionFilterAction::onToPositiveBtnClicked,
                modifier = Modifier
                    .weight(1f)
            )
        }

        Spacer(
            modifier = Modifier
                .height(24.dp)
        )

        Text(
            text = stringResource(R.string.by_type),
            color = Grey500,
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        ViewTypeSelector(
            selectedOption = transactionFilterState.selectedType,
            onSelectOption = transactionFilterAction::onTypeChange,
            label = null
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Text(
            text = stringResource(R.string.by_category),
            color = Grey500,
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        ViewCategoryDropdown(
            options = transactionFilterState.categories,
            selectedOptionId = transactionFilterState.selectedCategoryId,
            selectedOptionDisplay = transactionFilterState.selectedCategoryDisplay,
            onOptionSelected = transactionFilterAction::onCategorySelected,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp
                )
        )

        Spacer(
            modifier = Modifier
                .height(24.dp)
        )

        Button(
            onClick = { transactionFilterAction.onApplyFilter() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = stringResource(R.string.see_result),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun BottomSheetFilterPreview() {
    JetSpendingTheme {
        BottomSheetFilter(
            uiState = UiState.Idle,
            transactionFilterState = TransactionFilterState(
                timeFilter = TimeFilterOption.LAST_7_DAYS,
                timeFilterOptions = listOf(
                    FilterTimeOptionUI(TimeFilterOption.LAST_7_DAYS, R.string.last_7_days),
                    FilterTimeOptionUI(TimeFilterOption.THIS_MONTH, R.string.this_month),
                    FilterTimeOptionUI(TimeFilterOption.PICK_DATE, R.string.pick_date)
                ),
                displayFromAmount = "Rp 0",
                displayToAmount = "Rp 0",
                fromAmountInput = "",
                toAmountInput = ""
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