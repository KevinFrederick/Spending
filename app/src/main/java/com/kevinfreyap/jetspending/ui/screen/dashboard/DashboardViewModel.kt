package com.kevinfreyap.jetspending.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.utils.mapper.TransactionItemUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionUseCase: TransactionUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
    val latestTransactions = transactionUseCase.getLatestTransactions()
        .map { transactions ->
            transactions.map { transaction ->
                transactionItemUiMapper.mapTransactionDomainToUi(transaction)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}