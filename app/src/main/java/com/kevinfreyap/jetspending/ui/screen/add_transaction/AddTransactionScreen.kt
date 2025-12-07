package com.kevinfreyap.jetspending.ui.screen.add_transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.data.model.TransactionType
import com.kevinfreyap.jetspending.ui.components.BottomSheetInputAmount
import com.kevinfreyap.jetspending.ui.components.ViewAmountCard
import com.kevinfreyap.jetspending.ui.components.ViewCalendarInput
import com.kevinfreyap.jetspending.ui.components.ViewCategoryItem
import com.kevinfreyap.jetspending.ui.components.ViewCustomDateDialog
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewTypeSelector
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var transactionName by remember { mutableStateOf("") }

    // If close with button or other action put in stateful
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedOption by remember { mutableStateOf(TransactionType.SPENDING) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    AddTransactionContent(
        onBackClick = onBackClick,
        transactionName = transactionName,
        onTransactionNameChange = { newName ->
            transactionName = newName
        },
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onCloseBottomSheet = {
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                }
            }
        },
        onShowBottomSheet = {
            showBottomSheet = it
        },
        selectedOption = selectedOption,
        onSelectOption = { option ->
            selectedOption = option
        },
        showDatePicker = showDatePicker,
        datePickerState = datePickerState,
        onShowDatePicker = {
            showDatePicker = it
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    onBackClick: () -> Unit,
    transactionName: String,
    onTransactionNameChange: (String) -> Unit,
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onShowBottomSheet: (Boolean) -> Unit,
    onCloseBottomSheet: () -> Unit,
    selectedOption: TransactionType,
    onSelectOption: (TransactionType) -> Unit,
    showDatePicker: Boolean,
    datePickerState: DatePickerState,
    onShowDatePicker: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {


    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.add_transaction),
                onCurrencyIconClick = {},
                onBackClick = { onBackClick() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.transaction_name),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleMedium
            )

            ViewTextField(
                value = transactionName,
                onValueChange = onTransactionNameChange,
                label = stringResource(R.string.transaction_name)
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            ViewAmountCard(
                onTransactionAmountClick = {
                    onShowBottomSheet(true)
                },
                transactionAmount = "Rp 1.000.000"
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewTypeSelector(
                selectedOption = selectedOption,
                onSelectOption = onSelectOption,
            )

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Text(
                text = stringResource(R.string.category),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            FlowRow (
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                listOf(1,2,3,4,5,6,7,8,9,10).forEach{ item ->
                    ViewCategoryItem(
                        categoryImage = R.drawable.ic_salary_icon,
                        categoryName = "Salary",
                        onClick = {}
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Text(
                text = stringResource(R.string.date),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            ViewCalendarInput(
                value = "Today, 7 December 2025",
                onClick = {
                    onShowDatePicker(true)
                }
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Button (
                onClick = {  },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.save_transaction),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background,
                // User swipe or click background to close
                onDismissRequest = {
                    onShowBottomSheet(false)
                },
                sheetState = sheetState
            ) {
                BottomSheetInputAmount(
                    amountValue = "",
                    onPositiveClick = {},
                    onNegativeClick = onCloseBottomSheet
                )
            }
        }

        if (showDatePicker) {
            ViewCustomDateDialog(
                onDateSelected = { dateMillis ->

                },
                onDismiss = {
                    onShowDatePicker(false)
                },
                datePickerState = datePickerState
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    heightDp = 1500
)
@Composable
fun AddTransactionPreview() {
    JetSpendingTheme {
        AddTransactionContent(
            transactionName = "",
            onTransactionNameChange = {},
            onBackClick = {},
            showBottomSheet = false,
            onShowBottomSheet = { },
            sheetState = rememberModalBottomSheetState(),
            selectedOption = TransactionType.SPENDING,
            onSelectOption = {},
            onCloseBottomSheet = {},
            showDatePicker = false,
            datePickerState = rememberDatePickerState(),
            onShowDatePicker = {},
        )
    }
}