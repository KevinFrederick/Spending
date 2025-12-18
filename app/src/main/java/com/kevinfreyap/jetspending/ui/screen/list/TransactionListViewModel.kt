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
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.FilterBottomSheetType
import com.kevinfreyap.jetspending.ui.model.FilterTimeOptionUI
import com.kevinfreyap.jetspending.ui.model.TransactionsUi
import com.kevinfreyap.jetspending.ui.state.AmountInputState
import com.kevinfreyap.jetspending.ui.state.FilterDraftState
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import kotlin.collections.find

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    currencyUseCase: CurrencyUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
    val currencyCode = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _filter = MutableStateFlow(TransactionFilter())
    val filter: StateFlow<TransactionFilter> = _filter

    private val _draftState = MutableStateFlow(FilterDraftState())
    private val _amountInputState = MutableStateFlow(AmountInputState())

    private val _activeSheetContent = MutableStateFlow<FilterBottomSheetType>(FilterBottomSheetType.None)
    val activeSheetContent = _activeSheetContent.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

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
                .map { transaction ->
                    TransactionsUi.Item (
                        transactionItemUiMapper.mapTransactionDomainToUi(
                            transaction,
                            currencyCode.value
                        )
                    )
                }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _categoriesFlow = _draftState
        .map { it.selectedType }
        .distinctUntilChanged() // Prevent refetching if Type didn't change
        .flatMapLatest { type ->
            if (type != null) {
                categoryUseCase.getCategoryByType(type)
            } else {
                categoryUseCase.getAllCategories()
            }
        }

    val filterUiState: StateFlow<TransactionFilterState> = combine(
        _draftState,
        _categoriesFlow,
        _amountInputState,
        currencyCode
    ) { draft, categories, amount, currency ->

        val categoriesUiList = categories
            .map { CategoryUiFormatter.mapCategoryDomainToUi(it) }
            .sortedBy { it.sortOrder }

        val validCategory = categoriesUiList.find { it.id == draft.selectedCategoryId }

        TransactionFilterState(
            // Time
            earliestTransactionYear = transactionUseCase.earliestTransactionYear,
            timeFilter = draft.timeFilter,
            timeFilterOptions = dateFilterOptions,
            fromDateRaw = draft.tempStartDate,
            toDateRaw = draft.tempEndDate,
            fromDateDisplay = draft.tempStartDate?.let { DateFormatter.formatToDate(it) },
            toDateDisplay = draft.tempEndDate?.let { DateFormatter.formatToDate(it) },

            // Amount
            fromAmountInput = amount.fromAmountInput,
            toAmountInput = amount.toAmountInput,
            displayFromAmount = CurrencyUiFormatter.formatWithCode(draft.fromAmount.toPlainString(), currency),
            displayToAmount = CurrencyUiFormatter.formatWithCode(draft.toAmount.toPlainString(), currency),

            // Type & Category
            selectedType = draft.selectedType,
            categories = categoriesUiList,
            selectedCategoryId = validCategory?.id,
            selectedCategoryDisplay = validCategory?.let { CategoryUiFormatter.mapCategoryNameToString(it.id) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionFilterState(
            timeFilterOptions = dateFilterOptions
        )
    )

    // Query
    fun onQueryChange(query: String) {
        _query.value = query
    }

    // Time Logic
    fun onTimeFilterOptionClicked(option: TimeFilterOption) {
        _draftState.update { it.copy(timeFilter = option) }
    }

    // Date Logic
    fun onDateSelected(millis: Long?, isFrom: Boolean) {
        val date = millis?.let { Instant.ofEpochMilli(millis) }
        _draftState.update {
            if (isFrom) it.copy(tempStartDate = date) else it.copy(tempEndDate = date)
        }
    }

    fun initializeDateRange() {
        _draftState.update {
            it.copy(tempStartDate = it.startDate, tempEndDate = it.endDate)
        }
    }

    fun onSetDate() {
        _draftState.update {
            it.copy(startDate = it.tempStartDate, endDate = it.tempEndDate)
        }
        checkSelectedDate()
    }

    fun onResetDate() {
        _draftState.update {
            it.copy(tempStartDate = null, tempEndDate = null)
        }
    }

    // Amount Logic
    fun prepareAmountInput(isFrom: Boolean) {
        val confirmedAmount = if (isFrom) {
            _draftState.value.fromAmount
        } else {
            _draftState.value.toAmount
        }

        val amountInput = if (confirmedAmount > BigDecimal.ZERO) {
            confirmedAmount.toPlainString()
        } else {
            ""
        }

        _amountInputState.update { currencyInputs ->
            if (isFrom) {
                currencyInputs.copy(fromAmountInput = amountInput)
            } else {
                currencyInputs.copy(toAmountInput = amountInput)
            }
        }
    }

    fun onAmountChange(input: String, isFrom: Boolean) {
        val cleanInput = CurrencyUiFormatter.cleanAmount(input, currencyCode.value) ?: return

        _amountInputState.update {
            if (isFrom) it.copy(fromAmountInput = cleanInput)
            else  it.copy(toAmountInput = cleanInput)
        }

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_AMOUNT)
    }

    fun onSetAmount(isFrom: Boolean) {
        val currentAmountInputs = _amountInputState.value

        _draftState.update { draftState ->
            if (isFrom) {
                val validAmount = currentAmountInputs.fromAmountInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
                draftState.copy(
                    fromAmount = validAmount
                )
            } else {
                val validAmount = currentAmountInputs.toAmountInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
                draftState.copy(
                    toAmount = validAmount
                )
            }
        }
        validateAmounts(draft = null)
    }

    // Type Logic
    fun onTypeChange(type: TransactionType) {
        _draftState.update {
            val newType = if (it.selectedType == type) null else type
            it.copy(selectedType = newType, selectedCategoryId = null)
        }
    }

    // Category Logic
    fun onCategoryChange(categoryId: String) {
        _draftState.update {
            it.copy(selectedCategoryId = categoryId)
        }
    }

    // Core
    fun initializeFilter() {
        val currentFilter = _filter.value

        _draftState.value = FilterDraftState(
            timeFilter = currentFilter.timeFilter,
            startDate = currentFilter.customStartDate,
            endDate = currentFilter.customEndDate,
            fromAmount = currentFilter.fromAmount,
            toAmount = currentFilter.toAmount,
            selectedType = currentFilter.type,
            selectedCategoryId = currentFilter.category
        )

        _amountInputState.value = AmountInputState(
            fromAmountInput = if (currentFilter.fromAmount > BigDecimal.ZERO) currentFilter.fromAmount.toPlainString() else "",
            toAmountInput = if (currentFilter.toAmount > BigDecimal.ZERO) currentFilter.toAmount.toPlainString() else "",
        )
    }

    fun resetFilter() {
        _draftState.value = FilterDraftState()
    }

    fun applyFilter() {
        val draft = _draftState.value

        if (!validateAmounts(draft)) return

        val newFilter = TransactionFilter(
            timeFilter = draft.timeFilter,
            customStartDate = draft.startDate,
            customEndDate = draft.endDate,
            fromAmount = draft.fromAmount,
            toAmount = draft.toAmount,
            type = draft.selectedType,
            category = draft.selectedCategoryId
        )

        _filter.value = newFilter
    }

    // Validation
    fun checkSelectedDate() {
        val draft = _draftState.value
        if (draft.timeFilter == TimeFilterOption.PICK_DATE &&
            draft.startDate == null && draft.endDate == null){
            onTimeFilterOptionClicked(TimeFilterOption.ALL_TIME)
        }
    }

    private fun validateAmounts(draft: FilterDraftState?): Boolean {
        val from = draft?.fromAmount ?: _draftState.value.fromAmount
        val to = draft?.toAmount ?: _draftState.value.toAmount
        if ((from > BigDecimal.ZERO) && (to > BigDecimal.ZERO) && (from > to)) {
            _uiState.value = UiState.ValidationErrors(
                ErrorHelper.validationErrorsToUiError(
                    listOf(ValidationError.TransactionAmountFromGreaterThanTo)
                )
            )
            return false
        }
        return true
    }

    // Navigate
    fun navigateTo(content: FilterBottomSheetType) {
        if (content == FilterBottomSheetType.DateFilter) initializeDateRange()
        if (content == FilterBottomSheetType.AmountFrom) prepareAmountInput(isFrom = true)
        if (content == FilterBottomSheetType.AmountTo) prepareAmountInput(isFrom = false)

        _activeSheetContent.value = content
    }
}