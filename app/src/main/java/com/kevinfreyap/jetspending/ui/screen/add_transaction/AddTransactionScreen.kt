package com.kevinfreyap.jetspending.ui.screen.add_transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.model.AppCurrency

@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel(),
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
    val uiState by viewModel.uiState.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()

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
        },
        transactionAmountInput = transactionAmountInput,
        onTransactionAmountChange = { amount ->
            viewModel.onRawAmountChanged(amount)
        },
        onInitBottomSheet = {
            viewModel.onInitBottomSheet()
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
        categories = categories,
        onSaveBtnClicked = {
            viewModel.onSaveTransaction()
        },
        showSuccessDialog = showSuccessDialog,
        onDismissDialog = {
            viewModel.onDialogDismissed()
            onBackClick()
        },
        uiState = uiState,
        modifier = modifier,
    )
}