package com.kevinfreyap.jetspending.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.AmountData
import com.kevinfreyap.jetspending.ui.model.CategorySelectionData
import com.kevinfreyap.jetspending.ui.model.FilterTimeOptionUI
import com.kevinfreyap.jetspending.ui.model.TimeFilterData
import com.kevinfreyap.jetspending.ui.model.TransactionsUi
import com.kevinfreyap.jetspending.ui.state.TransactionFilterState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.ErrorHelper
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.kevinfreyap.jetspending.utils.mapper.TransactionItemUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import kotlin.collections.find

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
    val _currencyCode = AppCurrency.IDR

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _filter = MutableStateFlow(TransactionFilter())
    val filter: StateFlow<TransactionFilter> = _filter

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val transactions: Flow<PagingData<TransactionsUi>> = combine(
        flow = _query.debounce(300),
        flow2 = _filter
    ) { query, filter ->
        query to filter
    }
        .flatMapLatest { (query, filter) ->
            transactionUseCase.getTransactions(query, filter)
        }
        .map { pagingData ->
            pagingData
                .map { transaction -> TransactionsUi.Item (transactionItemUiMapper.mapTransactionDomainToUi(transaction)) }
                .insertSeparators { before: TransactionsUi.Item?, after: TransactionsUi.Item? ->
                    if (after == null) return@insertSeparators null

                    if (before == null) {
                        return@insertSeparators TransactionsUi.Header(
                            DateFormatter.formatToMonthYear(after.transaction.transactionDateRaw)
                        )
                    }

                    val beforeDate = DateFormatter.formatToMonthYear(before.transaction.transactionDateRaw)
                    val afterDate = DateFormatter.formatToMonthYear(after.transaction.transactionDateRaw)

                    if (beforeDate != afterDate){
                        TransactionsUi.Header(afterDate)
                    } else {
                        null
                    }
                }
        }
        .cachedIn(viewModelScope)

    private val dateFilterOptions = TimeFilterOption.entries
        .filter {
            it != TimeFilterOption.ALL_TIME
        }
        .map {
            FilterTimeOptionUI(
                id = it,
                label = when(it) {
                    TimeFilterOption.LAST_7_DAYS -> R.string.last_7_days
                    TimeFilterOption.THIS_MONTH -> R.string.this_month
                    TimeFilterOption.PICK_DATE -> R.string.pick_date
                    else -> 0
                }
            )
        }


    private val _timeFilter = MutableStateFlow(TimeFilterOption.ALL_TIME)
    private val _fromRawAmount = MutableStateFlow(BigDecimal.ZERO)
    private val _toRawAmount = MutableStateFlow(BigDecimal.ZERO)
    private val _fromAmountInput = MutableStateFlow("")
    private val _toAmountInput = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<TransactionType?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val categories = _selectedType
        .flatMapLatest { transactionType ->
            val rawFlow = if (transactionType != null) {
                categoryUseCase.getCategoryByType(transactionType)
            } else {
                categoryUseCase.getAllCategories()
            }

            rawFlow
                .map { categories ->
                    categories
                        .map {
                            CategoryUiFormatter.mapCategoryDomainToUi(it)
                        }
                        .sortedBy { it.sortOrder }
                }
        }

    private val _selectedCategoryId = MutableStateFlow<String?>(null)

    // On Change Date
    private val _selectedFromDateRaw = MutableStateFlow<Instant?>(null)
    private val _selectedToDateRaw = MutableStateFlow<Instant?>(null)

    // On Set Date
    private val _selectedFromDate = MutableStateFlow<Instant?>(null)
    private val _selectedToDate = MutableStateFlow<Instant?>(null)

    val timeState = combine(
        _timeFilter,
        _selectedFromDateRaw,
        _selectedToDateRaw
    ) { timeType, fromDate, toDate ->
        TimeFilterData(
            earliestTransactionYear = transactionUseCase.earliestTransactionYear,
            filterType = timeType,
            fromDate = fromDate,
            toDate = toDate,
            displayFromText = if (timeType == TimeFilterOption.PICK_DATE) fromDate?.let { DateFormatter.formatToDate(it) } else null,
            displayToText = if (timeType == TimeFilterOption.PICK_DATE) toDate?.let { DateFormatter.formatToDate(it) } else null
        )
    }

    val amountState = combine(
        _fromAmountInput,
        _toAmountInput,
        _fromRawAmount,
        _toRawAmount
    ) { fromInput, toInput, fromRaw, toRaw ->
        AmountData(
            fromInput = fromInput,
            toInput = toInput,
            fromRaw = fromRaw,
            toRaw = toRaw
        )
    }

    val categoryState = combine(
        categories,
        _selectedCategoryId
    ) { categoryList, categoryId ->
        val validCategory = categoryList.find { it.id == categoryId }

        val categoryDisplay = if (validCategory != null){
            CategoryUiFormatter.mapIconNameToString(validCategory.id)
        } else {
            null
        }

        CategorySelectionData(
            list = categoryList,
            validId = validCategory?.id,
            displayText = categoryDisplay
        )
    }

    val filterState = combine(
        timeState,
        amountState,
        _selectedType,
        categoryState
    ) { timeState, amountState, type, categoryState ->

        val formattedFrom = CurrencyUiFormatter.formatWithCode(amountState.fromRaw.toPlainString(), _currencyCode)
        val formattedTo = CurrencyUiFormatter.formatWithCode(amountState.toRaw.toPlainString(), _currencyCode)

        TransactionFilterState(
            earliestTransactionYear = timeState.earliestTransactionYear,
            timeFilter = timeState.filterType,
            fromDateRaw = timeState.fromDate,
            toDateRaw = timeState.toDate,
            fromDateDisplay = timeState.displayFromText,
            toDateDisplay = timeState.displayToText,
            timeFilterOptions = dateFilterOptions,
            displayFromAmount = formattedFrom,
            displayToAmount = formattedTo,
            fromAmountInput = amountState.fromInput,
            toAmountInput = amountState.toInput,
            selectedType = type,
            categories = categoryState.list,
            selectedCategoryId = categoryState.validId,
            selectedCategoryDisplay = categoryState.displayText
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionFilterState(
            timeFilterOptions = dateFilterOptions
        )
    )

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()


    init {
        // Keeps Running
        viewModelScope.launch {
            _selectedType.collect {
                _selectedCategoryId.value = null
            }
        }
    }


    fun onTimeFilterOptionClicked(option: TimeFilterOption) {
        _timeFilter.value = option
    }

    fun onFromDateSelected(millis: Long?) {
        if (millis != null) {
            _selectedFromDateRaw.value = Instant.ofEpochMilli(millis)
        }
    }

    fun onToDateSelected(millis: Long?) {
        if (millis != null) {
            _selectedToDateRaw.value = Instant.ofEpochMilli(millis)
        }
    }

    fun initializeDateRange() {
        _selectedFromDateRaw.value = _selectedFromDate.value
        _selectedToDateRaw.value = _selectedToDate.value
    }

    fun onSetDateRange() {
        _selectedFromDate.value = _selectedFromDateRaw.value
        _selectedToDate.value = _selectedToDateRaw.value
        checkSelectedDate()
    }

    fun onResetDateRange() {
        _selectedFromDateRaw.value = null
        _selectedToDateRaw.value = null
    }

    fun onFromRawAmountChange(amount: String) {
        val cleanAmount = CurrencyUiFormatter.cleanAmount(amount, _currencyCode)
        if (cleanAmount == null) return

        _fromAmountInput.value = cleanAmount
        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_AMOUNT)
    }

    fun onToRawAmountChange(amount: String) {
        val cleanAmount = CurrencyUiFormatter.cleanAmount(amount, _currencyCode)
        if (cleanAmount == null) return

        _toAmountInput.value = cleanAmount
        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_AMOUNT)
    }

    fun onFromAmountPositiveBtnClicked() {
        val trimmedZeroFraction = CurrencyUiFormatter.trimFractionZero(_fromAmountInput.value)
        _fromRawAmount.value = trimmedZeroFraction.toBigDecimalOrNull() ?: BigDecimal.ZERO
        checkSelectedAmount()
    }

    fun onToAmountPositiveBtnClicked() {
        val trimmedZeroFraction = CurrencyUiFormatter.trimFractionZero(_toAmountInput.value)
        _toRawAmount.value = trimmedZeroFraction.toBigDecimalOrNull() ?: BigDecimal.ZERO
        checkSelectedAmount()
    }

    fun onTypeChange(type: TransactionType) {
        if (_selectedType.value == type){
            _selectedType.value = null
        } else {
            _selectedType.value = type
        }
    }

    fun onCategorySelected(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun initializeFilter() {
        // Time Option
        _timeFilter.value = _filter.value.timeFilter
        _selectedFromDate.value = _filter.value.customStartDate
        _selectedToDate.value = _filter.value.customEndDate
        // Amount Input Field
        if (_filter.value.fromAmount > BigDecimal.ZERO) {
            onToRawAmountChange(_filter.value.toAmount.toPlainString())
            onFromRawAmountChange(_filter.value.fromAmount.toPlainString())
        } else {
            _fromAmountInput.value = ""
            _toAmountInput.value = ""
        }
        // Amount Input Raw
        _fromRawAmount.value = _filter.value.fromAmount
        _toRawAmount.value = _filter.value.toAmount
        // Type
        _selectedType.value = _filter.value.type
        // Category
        _selectedCategoryId.value = _filter.value.category
    }

    fun applyFilter() {
        val transactionFilter = TransactionFilter(
            timeFilter = _timeFilter.value,
            customStartDate = _selectedFromDate.value,
            customEndDate = _selectedToDate.value,
            fromAmount = _fromRawAmount.value,
            toAmount = _toRawAmount.value,
            type = _selectedType.value,
            category = _selectedCategoryId.value
        )

        _filter.value = transactionFilter
    }

    fun resetFilter() {
        // Time Option
        _timeFilter.value = TimeFilterOption.ALL_TIME
        _selectedFromDateRaw.value = null
        _selectedToDateRaw.value = null
        _selectedFromDate.value = null
        _selectedToDate.value = null
        // Amount Input Field
        _fromAmountInput.value = ""
        _toAmountInput.value = ""
        // Amount Input Raw
        _fromRawAmount.value = BigDecimal.ZERO
        _toRawAmount.value = BigDecimal.ZERO
        // Type
        _selectedType.value = null
        // Category
        _selectedCategoryId.value = null
    }

    fun checkSelectedDate() {
        if ((_timeFilter.value == TimeFilterOption.PICK_DATE) && (_selectedFromDate.value == null && _selectedToDate.value == null)) {
            _timeFilter.value = TimeFilterOption.ALL_TIME
        }
    }

    fun checkSelectedAmount() {
        val fromAmount = _fromRawAmount.value
        val toAmount = _toRawAmount.value
        if (fromAmount > BigDecimal.ZERO && toAmount > BigDecimal.ZERO) {
            if (fromAmount > toAmount) {
                _uiState.value = UiState.ValidationErrors(
                    ErrorHelper.validationErrorsToUiError(
                        listOf(ValidationError.TransactionAmountFromGreaterThanTo)
                    )
                )
            }
        }
    }
}