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

    private val _currencyCode = MutableStateFlow<AppCurrency?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _transactionFlow = _transactionId
        .flatMapLatest { id ->
            transactionUseCase.getTransactionById(id)
        }

    val transactionState: StateFlow<TransactionDetailState?> = combine(
        _transactionFlow,
        _currencyCode
    ) { transaction, userSelectedCurrency ->
        val currency = userSelectedCurrency ?: transaction?.transaction?.currency

        if (transaction == null || currency == null) {
            return@combine TransactionDetailState()
        }

        val calculatedRates = currencyUseCase.calculateAmountBasedOnRates(
            amount = transaction.transaction.amount,
            sourceCurrency = transaction.transaction.currency,
            targetCurrency = currency,
            rates = transaction.rates
        ) ?: BigDecimal.ZERO

        TransactionDetailState(
            transactionName = transaction.transaction.name,
            transactionAmountDisplay = transactionItemUiMapper.formatAmountType(
                transactionAmount = calculatedRates,
                transactionType = transaction.transaction.type,
                selectedCurrency = currency
            ),
            transactionCurrency = currency,
            transactionDateDisplay = DateFormatter.formatInstantToDateHour(transaction.transaction.date),
            transactionType = transaction.transaction.type,
            transactionCategory = CategoryUiFormatter.mapCategoryDomainToUi(transaction.transaction.category),
            transactionAmountRaw = calculatedRates,
            transactionDateRaw = transaction.transaction.date,
            transactionNotes = transaction.transaction.notes,
            transactionColor = if (transaction.transaction.type == TransactionType.INCOME) Green500 else Orange700
        )
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

    fun onSelectCurrency(currency: AppCurrency) {
        _currencyCode.value = currency
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