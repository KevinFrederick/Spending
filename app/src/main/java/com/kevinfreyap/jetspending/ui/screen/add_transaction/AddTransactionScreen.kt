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
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.state.TransactionAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel(),
) {
    val transactionId by viewModel.transactionId.collectAsState()
    val currencyCode by viewModel.currencyCode.collectAsState()

    val transactionState by viewModel.transactionState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()
    val showFailureDialog by viewModel.showFailureDialog.collectAsState()

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showAmountSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun closeBottomSheet() {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible){
                showAmountSheet = false
            }
        }
    }

    val transactionAction = remember (viewModel) {
        object : TransactionAction {
            override fun onNameChange(name: String) {
                viewModel.onNameChange(name)
            }

            override fun onAmountChange(amount: String) {
                viewModel.onAmountChange(amount)
            }

            override fun onSetAmount() {
                viewModel.onSetAmount()
                closeBottomSheet()
            }

            override fun initializeAmount() {
                viewModel.initializeAmount()
            }

            override fun onSelectType(type: TransactionType) {
                viewModel.onSelectType(type)
            }

            override fun onSelectCategory(categoryId: String) {
                viewModel.onSelectCategory(categoryId)
            }

            override fun onDateSelected(millis: Long?) {
                viewModel.onDateSelected(millis)
            }

            override fun onNotesChange(notes: String) {
                viewModel.onNotesChange(notes)
            }

            override fun onSaveTransaction() {
                if (transactionId.isNullOrBlank()) {
                    viewModel.onSaveTransaction()
                } else {
                    viewModel.onUpdateTransaction()
                }
            }

            override fun onDismissSuccessDialog() {
                viewModel.onDialogSuccessDismissed()
                onBackClick()
            }

            override fun onDismissFailureDialog() {
                viewModel.onDialogFailureDismissed()
                onBackClick()
            }

        }
    }

    AddTransactionContent(
        transactionId = transactionId,
        currencyCode = transactionState.transactionCurrency ?: currencyCode,
        transactionState = transactionState,
        transactionAction = transactionAction,
        showSuccessDialog = showSuccessDialog,
        showFailureDialog = showFailureDialog,
        showConfirmationDialog = showConfirmationDialog,
        uiState = uiState,
        amountSheetState = sheetState,
        showAmountSheet = showAmountSheet,
        onBackClick = onBackClick,
        onSelectCurrency = {
            viewModel.onSelectCurrency(it)
        },
        onShowConfirmationDialog = {
            showConfirmationDialog = it
        },
        onShowAmountSheet = {
            showAmountSheet = true
        },
        onDismissAmountSheet = {
            closeBottomSheet()
        },
        modifier = modifier,
    )
}