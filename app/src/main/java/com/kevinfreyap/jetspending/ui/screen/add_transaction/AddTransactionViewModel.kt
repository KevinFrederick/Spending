package com.kevinfreyap.jetspending.ui.screen.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.jetspending.utils.mapper.CategoryUiMapper
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
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

    init {
        getCategories(_type.value)
    }

    // Name
    fun onNameChange(name: String) {
        _transactionName.value = name
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
    }

    // Date
    fun onDateSelected(millis: Long?) {
        if (millis != null){
            _selectedDate.value = Instant.ofEpochMilli(millis)
        }
    }
}