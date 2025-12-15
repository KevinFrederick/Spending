package com.kevinfreyap.jetspending.ui.screen.add_transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.ErrorHelper
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
): ViewModel(){
    private val _currencyCode = AppCurrency.IDR

    // Name
    private val _transactionName = MutableStateFlow("")
    val transactionName: StateFlow<String> = _transactionName

    // Amount
    private val _transactionRawAmount = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)

    val transactionAmountFormatted: StateFlow<String> = _transactionRawAmount
        .map {
            CurrencyUiFormatter.formatWithCode(it.toPlainString(), _currencyCode)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _transactionAmountInput = MutableStateFlow("")
    val transactionAmountInput: StateFlow<String> = _transactionAmountInput

    // Type
    private val _type = MutableStateFlow(TransactionType.SPENDING)
    val type: StateFlow<TransactionType> = _type

    // Categories
    private val _categories = MutableStateFlow<List<CategoryUI>>(emptyList())
    val categories: StateFlow<List<CategoryUI>> = _categories

    private val _selectedCategory = MutableStateFlow<CategoryUI?>(null)
    val selectedCategory: StateFlow<CategoryUI?> = _selectedCategory

    // Date
    private val _selectedDate = MutableStateFlow<Instant>(Instant.now())
    val selectedDate: StateFlow<Instant> = _selectedDate

    val selectedDateText: StateFlow<String> = _selectedDate
        .map { DateFormatter.formatToDateWithDay(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DateFormatter.formatToDateWithDay(Instant.now())
        )

    // UiState
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Success
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog = _showSuccessDialog.asStateFlow()

    init {
        getCategories(_type.value)
    }

    // Name
    fun onNameChange(name: String) {
        _transactionName.value = name

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_NAME)
    }

    // Amount
    fun onInitBottomSheet() {
        val current = _transactionRawAmount.value
        _transactionAmountInput.value = if (current == BigDecimal.ZERO) "" else current.toPlainString()
    }

    fun onRawAmountChanged(amount: String) {
        val cleanAmount = CurrencyUiFormatter.cleanAmount(amount, _currencyCode)

        if (cleanAmount == null) return

        _transactionAmountInput.value = cleanAmount

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_AMOUNT)
    }

    fun onPositiveBtnAmount() {
        val trimmedFractionZero = CurrencyUiFormatter.trimFractionZero(_transactionAmountInput.value)

        _transactionRawAmount.value = trimmedFractionZero.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    // Type
    fun setType(type: TransactionType) {
        _type.value = type
        getCategories(type)
    }

    /// Categories
    fun getCategories(type: TransactionType) {
        viewModelScope.launch {
            categoryUseCase.getCategoryByType(type).collect { categories ->
                _categories.value = categories
                    .map { category ->
                        CategoryUiFormatter.mapCategoryDomainToUi(category)
                    }
                    .sortedBy { it.sortOrder }
            }
        }
    }

    fun onSelectCategory(item: CategoryUI) {
        _selectedCategory.value = item

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_CATEGORY)
    }

    // Date
    fun onDateSelected(millis: Long?) {
        if (millis != null){
            _selectedDate.value = Instant.ofEpochMilli(millis)
        }
    }

    // Save Transaction
    fun validateTransaction(
        name: String,
        amount: BigDecimal,
        category: CategoryUI?
    ): List<ValidationError> {
        val currentErrors = mutableListOf<ValidationError>()

        if (name.isBlank()) currentErrors.add(ValidationError.TransactionNameRequired)
        if (amount <= BigDecimal.ZERO) currentErrors.add(ValidationError.TransactionAmountInvalid)
        if (category == null) currentErrors.add(ValidationError.TransactionCategoryMissing)

        return currentErrors
    }

    fun onSaveTransaction() {
        _uiState.value = UiState.Loading

        val validationRes = validateTransaction(
            name = _transactionName.value,
            amount = _transactionRawAmount.value,
            category = _selectedCategory.value
        )

        if (validationRes.isNotEmpty()) {
            _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(validationRes))
            return
        }

        val transactionCategoryId = _selectedCategory.value?.id ?: ""

        viewModelScope.launch {
            val result = transactionUseCase.insertTransaction(
                name = _transactionName.value,
                amount = _transactionRawAmount.value,
                type = _type.value,
                categoryId = transactionCategoryId,
                date = _selectedDate.value
            )

            when(result) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showSuccessDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                }
                is DomainResult.Failure -> {
                    Log.e(VIEWMODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun onDialogDismissed() {
        _showSuccessDialog.value = false
    }

    companion object {
        private const val VIEWMODEL_TAG = "AddTransactionViewModel"
    }
}