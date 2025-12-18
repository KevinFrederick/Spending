package com.kevinfreyap.jetspending.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.utils.mapper.TransactionItemUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionUseCase: TransactionUseCase,
    currencyUseCase: CurrencyUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
    val currencyCode = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val latestTransactions = currencyCode
        .flatMapLatest { currency ->
            transactionUseCase.getLatestTransactions()
                .map { transactions ->
                    transactions.map { transaction ->
                        transactionItemUiMapper.mapTransactionDomainToUi(transaction, currency)
                    }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}