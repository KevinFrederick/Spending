package com.kevinfreyap.jetspending.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.model.TransactionsUi
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.kevinfreyap.jetspending.utils.mapper.TransactionItemUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
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
}