package com.kevinfreyap.jetspending.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.state.TransactionDetailState
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.kevinfreyap.jetspending.utils.mapper.TransactionItemUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val currencyUseCase: CurrencyUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel(){
    private val _transactionId = MutableStateFlow("")
    private val currencyCode = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionState: StateFlow<TransactionDetailState?> = combine(
        _transactionId,
        currencyCode
    ) { transactionId, currency ->
        Pair(transactionId, currency)
    }
        .flatMapLatest { (transactionId, currency) ->
            transactionUseCase.getTransactionById(transactionId)
                .map { transactionWithRates ->

                    transactionWithRates?.let {
                        val calculatedRates = currencyUseCase.calculateAmountBasedOnRates(
                            amount = transactionWithRates.transaction.amount,
                            sourceCurrency = transactionWithRates.transaction.currency,
                            targetCurrency = currency,
                            rates = transactionWithRates.rates
                        ) ?: BigDecimal.ZERO

                        TransactionDetailState(
                            transactionName = it.transaction.name,
                            transactionAmountDisplay = transactionItemUiMapper.formatAmountType(
                                transactionAmount = calculatedRates,
                                transactionType = it.transaction.type,
                                selectedCurrency = currency
                            ),
                            transactionDateDisplay = DateFormatter.formatInstantToDateHour(it.transaction.date),
                            transactionType = it.transaction.type,
                            transactionCategory = CategoryUiFormatter.mapCategoryDomainToUi(it.transaction.category),
                            transactionAmountRaw = calculatedRates,
                            transactionDateRaw = it.transaction.date,
                            transactionNotes = it.transaction.notes,
                            transactionColor = if (it.transaction.type == TransactionType.INCOME) Green500 else Orange700
                        )
                    }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionDetailState()
        )

    private val _isDelete = MutableStateFlow(false)
    val isDelete = _isDelete.asStateFlow()

    private val _showDeleteSuccessDialog = MutableStateFlow(false)
    val showDeleteSuccessDialog = _showDeleteSuccessDialog.asStateFlow()

    fun onSetTransactionId(id: String) {
        _transactionId.value = id
    }

    fun onDeleteTransaction() {
        if (_transactionId.value.isNotBlank()) {
            viewModelScope.launch {
                _isDelete.value = true

                transactionUseCase.deleteTransaction(_transactionId.value)

                withContext(Dispatchers.Main) {
                    _showDeleteSuccessDialog.value = true
                }
            }
        }
    }
}