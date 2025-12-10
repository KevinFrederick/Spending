package com.kevinfreyap.jetspending.ui.screen.add_transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevinfreyap.domain.model.AppCurrency
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    navController: NavController,
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
    val errors by viewModel.errors.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()

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
        onSaveBtnClicked = {
            viewModel.onSaveTransaction()
        },
        isLoading = loading,
        showSuccessDialog = showSuccessDialog,
        onDismissDialog = {
            viewModel.onDialogDismissed()
            navController.popBackStack()
        },
        errors = errors,
        modifier = modifier,
    )
}