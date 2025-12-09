package com.kevinfreyap.jetspending.ui.screen.add_transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.components.BottomSheetInputAmount
import com.kevinfreyap.jetspending.ui.components.ViewAmountCard
import com.kevinfreyap.jetspending.ui.components.ViewCalendarInput
import com.kevinfreyap.jetspending.ui.components.ViewCategoryItem
import com.kevinfreyap.jetspending.ui.components.ViewCustomDateDialog
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewTypeSelector
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.CurrencyVisualTransformation
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val currencyCode = AppCurrency.IDR

    val transactionName by viewModel.transactionName.collectAsState()
    val transactionAmountInput by viewModel.transactionAmountInput.collectAsState()
    val transactionAmountFormatted by viewModel.transactionAmountFormatted.collectAsState()
    val type by viewModel.type.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val date by viewModel.selectedDate.collectAsState()
    val dateText by viewModel.selectedDateText.collectAsState()

    // If close with button or other action put in stateful
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }

    fun closeBottomSheet() {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

    AddTransactionContent(
        onBackClick = onBackClick,
        transactionName = transactionName,
        onTransactionNameChange = { newName ->
            viewModel.onNameChange(newName)
        },
        currencyCode = currencyCode,
        transactionAmountFormatted = transactionAmountFormatted,
        onPositiveBtnBottomSheet = {
            viewModel.onPositiveBtnAmount()
            closeBottomSheet()
        },
        onNegativeBtnBottomSheet = {
            closeBottomSheet()
        },
        transactionAmountInput = transactionAmountInput,
        onTransactionAmountChange = { amount ->
            viewModel.onRawAmountChanged(amount)
        },
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onInitBottomSheet = {
            viewModel.onInitBottomSheet()
        },
        onShowBottomSheet = {
            showBottomSheet = it
        },
        selectedOption = type,
        onSelectOption = { option ->
            viewModel.setType(option)
        },
        selectedCategory = selectedCategory,
        onSelectedCategory = { category ->
            viewModel.onSelectCategory(category)
        },
        rawDate = date,
        dateText = dateText,
        onDateSelected = { dateMillis ->
            viewModel.onDateSelected(dateMillis)
        },
        showDatePicker = showDatePicker,
        onShowDatePicker = {
            showDatePicker = it
        },
        categories = categories,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    onBackClick: () -> Unit,
    transactionName: String,
    onTransactionNameChange: (String) -> Unit,
    currencyCode: AppCurrency,
    transactionAmountFormatted: String,
    onPositiveBtnBottomSheet: () -> Unit,
    onNegativeBtnBottomSheet: () -> Unit,
    transactionAmountInput: String,
    onTransactionAmountChange: (String) -> Unit,
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onInitBottomSheet: () -> Unit,
    onShowBottomSheet: (Boolean) -> Unit,
    selectedOption: TransactionType,
    onSelectOption: (TransactionType) -> Unit,
    selectedCategory: CategoryUI?,
    onSelectedCategory: (CategoryUI) -> Unit,
    rawDate: Instant,
    dateText: String,
    onDateSelected: (Long?) -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: (Boolean) -> Unit,
    categories: List<CategoryUI>,
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
                    onInitBottomSheet()
                    onShowBottomSheet(true)
                },
                transactionAmount = transactionAmountFormatted
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
                categories.forEach{ item ->
                    val isSelected = item == selectedCategory

                    ViewCategoryItem(
                        categoryImage = item.iconRes,
                        categoryName = item.name,
                        isSelected = isSelected,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onSelectedCategory(item)
                                }
                            )
                    )
                }

                if (categories.size == 2 || (categories.size % 3 == 2)) {
                    Spacer(
                        modifier.size(100.dp)
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
                value = dateText,
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
                    amountInputSlot = {
                        ViewTextField(
                            value = transactionAmountInput,
                            onValueChange = onTransactionAmountChange,
                            label = stringResource(R.string.amount),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = CurrencyVisualTransformation(currencyCode),
                            modifier = Modifier
                                .padding(
                                    top = 4.dp
                                )
                        )
                    },
                    onPositiveClick = onPositiveBtnBottomSheet,
                    onNegativeClick = onNegativeBtnBottomSheet
                )
            }
        }

        if (showDatePicker) {
            val today = LocalDate.now()
            val startYear = today.minusYears(5)

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = rawDate.toEpochMilli(),
                yearRange = startYear.year..today.year,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val dayToCheck = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        return !dayToCheck.isBefore(startYear) && !dayToCheck.isAfter(today)
                    }
                }
            )

            ViewCustomDateDialog(
                onDateSelected = onDateSelected,
                onDismiss = {
                    onShowDatePicker(false)
                },
                datePickerState = datePickerState
            )
        }

    }
}