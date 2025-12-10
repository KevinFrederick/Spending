package com.kevinfreyap.jetspending.ui.screen.add_transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.ErrorMessage
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.utils.mapper.CategoryUiMapper
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.kevinfreyap.jetspending.utils.formatter.ErrorFormatter
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
    private val categoryUiMapper: CategoryUiMapper
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

    // Error
    private val _errors = MutableStateFlow<Map<Field, ErrorMessage>>(emptyMap())
    val errors: StateFlow<Map<Field, Int>> = _errors
        .map { values ->
            values.mapValues { entry ->
                ErrorFormatter.getErrorMessage(entry.value)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Success
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog = _showSuccessDialog.asStateFlow()

    init {
        getCategories(_type.value)
    }

    // Name
    fun onNameChange(name: String) {
        _transactionName.value = name

        if (_errors.value.containsKey(Field.TRANSACTION_NAME)) {
            val newErrors = _errors.value.toMutableMap()
            newErrors.remove(Field.TRANSACTION_NAME)
            _errors.value = newErrors
        }
    }

    // Amount
    fun onInitBottomSheet() {
        val current = _transactionRawAmount.value
        _transactionAmountInput.value = if (current == BigDecimal.ZERO) "" else current.toPlainString()
    }

    fun onRawAmountChanged(amount: String) {
        var cleanAmount = amount.replace(',', '.')

        val decimalIndex = cleanAmount.indexOf('.')
        if (decimalIndex >= 0) {
            val decimalsAfterDot = cleanAmount.substring(decimalIndex + 1)
            if (decimalsAfterDot == "00") return
            if (decimalsAfterDot.length > 2) return
        }

        if (cleanAmount.count { it == '.' } > 1) return
        cleanAmount = cleanAmount.filter { it.isDigit() || it == '.' }

        _transactionAmountInput.value = cleanAmount

        if (_errors.value.containsKey(Field.TRANSACTION_AMOUNT)) {
            val newErrors = _errors.value.toMutableMap()
            newErrors.remove(Field.TRANSACTION_AMOUNT)
            _errors.value = newErrors
        }
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
                        categoryUiMapper.mapCategoryDomainToUi(category)
                    }
                    .sortedBy { it.sortOrder }
            }
        }
    }

    fun onSelectCategory(item: CategoryUI) {
        _selectedCategory.value = item

        if (_errors.value.containsKey(Field.TRANSACTION_CATEGORY)) {
            val newErrors = _errors.value.toMutableMap()
            newErrors.remove(Field.TRANSACTION_CATEGORY)
            _errors.value = newErrors
        }
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
    ) {
        val currentErrors = _errors.value.toMutableMap()

        if (name.isBlank()) {
            currentErrors[Field.TRANSACTION_NAME] = ErrorMessage.TRANSACTION_NAME_REQUIRED
        } else {
            currentErrors.remove(Field.TRANSACTION_NAME)
        }

        if (amount <= BigDecimal.ZERO){
            currentErrors[Field.TRANSACTION_AMOUNT] = ErrorMessage.TRANSACTION_AMOUNT_ZERO
        } else {
            currentErrors.remove(Field.TRANSACTION_AMOUNT)
        }

        if (category == null) {
            currentErrors[Field.TRANSACTION_CATEGORY] = ErrorMessage.TRANSACTION_CATEGORY_NOT_SELECTED
        } else {
            currentErrors.remove(Field.TRANSACTION_CATEGORY)
        }

        _errors.value = currentErrors
    }

    fun onSaveTransaction() {
        validateTransaction(
            name = _transactionName.value,
            amount = _transactionRawAmount.value,
            category = _selectedCategory.value
        )
        if (_errors.value.isEmpty()) {
            val transactionCategoryId = _selectedCategory.value?.id ?: ""

            _isLoading.value = true

            viewModelScope.launch {
                try {
                    val result = transactionUseCase.insertTransaction(
                        name = _transactionName.value,
                        amount = _transactionRawAmount.value,
                        type = _type.value,
                        categoryId = transactionCategoryId,
                        date = _selectedDate.value
                    )

                    when(result) {
                        is DomainResult.Success -> {
                            _showSuccessDialog.value = true
                            _errors.value = emptyMap()
                        }
                        is DomainResult.Failure -> {
                            Log.e(VIEWMODEL_TAG, result.throwable.message ?: "Something Wrong")
                        }
                        is DomainResult.ValidationFailed -> {
                            _errors.value = result.errors.associate { it.field to it.message }
                        }
                    }
                } finally {
                    _isLoading.value = false
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