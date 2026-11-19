package com.kevinfreyap.jetspending.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.kevinfreyap.domain.usecase.category.GetAllCategoryUseCase
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.GetTransactionsUseCase
import com.kevinfreyap.jetspending.ui.action.TransactionListAction
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.helper.filter.toDomain
import com.kevinfreyap.jetspending.ui.helper.state.toTransactionItemState
import com.kevinfreyap.jetspending.ui.model.TransactionsUI
import com.kevinfreyap.jetspending.ui.state.TransactionListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    getCurrency: GetCurrencyUseCase,
    private val getTransactions: GetTransactionsUseCase,
    private val getAllCategory: GetAllCategoryUseCase
): ViewModel() {
    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val transactions: Flow<PagingData<TransactionsUI>> = combine(
        flow = _state
            .map { it.query }
            .debounce(300.milliseconds)
            .distinctUntilChanged(),
        flow2 = _state
            .map { it.filterState }
            .distinctUntilChanged(),
        flow3 = getCurrency()
            .distinctUntilChanged()
    ) { query, filterState, currency ->
        Triple(query, filterState, currency)
    }.flatMapLatest { (query, filterState, currency) ->
        getTransactions(
            query = query,
            filter = filterState.toDomain(),
            selectedCurrency = currency
        ).map { pagingData ->
            pagingData
                .map { converted ->
                    TransactionsUI.Item(
                        converted.toTransactionItemState(currency)
                    )
                }
                .insertSeparators { before: TransactionsUI.Item?, after: TransactionsUI.Item? ->
                    if (after == null) return@insertSeparators null

                    val afterMonthYear = after.transaction.transactionMonthYear

                    // First item at the top of the list
                    if (before == null) {
                        return@insertSeparators TransactionsUI.Header(
                            afterMonthYear
                        )
                    }
                    val beforeMonthYear = before.transaction.transactionMonthYear

                    if (beforeMonthYear != afterMonthYear) {
                        TransactionsUI.Header(afterMonthYear)
                    } else {
                        null
                    }
                }
        }
    }.cachedIn(viewModelScope)

    init {
        loadCategories()
    }

    fun onAction(action: TransactionListAction) {
        when(action) {
            is TransactionListAction.OnApplyFilter -> {
                _state.update { currentState ->
                    currentState.copy(
                        filterState = action.filter
                    )
                }
            }
            is TransactionListAction.OnQueryChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        query = action.query
                    )
                }
            }
            is TransactionListAction.OnToggleFilterSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isFilterSheetVisible = action.isVisible
                    )
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getAllCategory()
                .map { categories -> categories.map { it.toCategoryUI() } }
                .collect { categories ->
                    _state.update { currentState ->
                        currentState.copy(
                            categories = categories
                        )
                    }
                }
        }
    }
}