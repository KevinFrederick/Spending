package com.kevinfreyap.jetspending.ui.screen.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.DeleteTransactionUseCase
import com.kevinfreyap.domain.usecase.transaction.GetTransactionByIdUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.TransactionDetailAction
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.date.toFullDateLongMonth
import com.kevinfreyap.jetspending.ui.navigation.TransactionDetailNavigation
import com.kevinfreyap.jetspending.ui.screen.detail.state.TransactionDetailSummaryState
import com.kevinfreyap.jetspending.ui.state.TransactionDetailState
import com.kevinfreyap.jetspending.ui.state.TransactionDetailUIState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.UiEvent
import com.kevinfreyap.jetspending.utils.UiEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionById: GetTransactionByIdUseCase,
    private val getCurrency: GetCurrencyUseCase,
    private val calculateAmount: CalculateAmountBasedOnRatesUseCase,
    private val deleteTransaction: DeleteTransactionUseCase
): ViewModel(){
    val transactionId: String? = savedStateHandle["transactionId"]

    private val _uiEvent = Channel<UiEvent<TransactionDetailNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(TransactionDetailUIState(transactionId = transactionId))
    val state: StateFlow<TransactionDetailUIState> = _state.asStateFlow()

    private val _isExchangeRateFlipped = MutableStateFlow(false)

    init {
        loadTransaction()
    }

    fun onAction(action: TransactionDetailAction) {
        when(action) {
            is TransactionDetailAction.OnToggleDeleteDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDeleteDialogVisible = action.isVisible
                    )
                }
            }
            is TransactionDetailAction.OnSwapExchangeRate -> {
                _isExchangeRateFlipped.update { !it }
            }

            is TransactionDetailAction.OnConfirmDelete -> {
                if (_state.value.isDeleting || transactionId == null) return

                _state.update { currentState ->
                    currentState.copy(
                        isDeleting = true
                    )
                }

                viewModelScope.launch {
                    try {
                        deleteTransaction(transactionId)

                        _uiEvent.send(ShowToast(R.string.success_message_transaction_deleted))
                        _uiEvent.send(Navigate(TransactionDetailNavigation.NavigateUp))
                    } catch (e: Exception) {
                        Log.e(VIEW_MODEL_TAG, e.message ?: "Something Wrong")
                        _state.update { currentState ->
                            currentState.copy(
                                isDeleting = false,
                                isDeleteDialogVisible = false
                            )
                        }
                        _uiEvent.send(ShowToast(R.string.error_something_went_wrong))
                    }
                }
            }
        }
    }

    private fun loadTransaction() {
        if (transactionId.isNullOrBlank()) {
            _state.update { currentState ->
                currentState.copy(
                    detailState = UiState.Failure(
                        IllegalArgumentException()
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    detailState = UiState.Loading
                )
            }

            combine(
                getTransactionById(transactionId = transactionId),
                getCurrency(),
                _isExchangeRateFlipped
            ) { transactionWithRates, selectedCurrency, isFlipped ->
                if (transactionWithRates != null) {
                    val transaction = transactionWithRates.transaction
                    val rates = transactionWithRates.rates

                    val convertedAmount = calculateAmount(
                        amount = transaction.amount,
                        sourceCurrency = transaction.currency,
                        targetCurrency = selectedCurrency,
                        rates = rates
                    )

                    val originalAmountFormatted =
                        "${transaction.amount.toCurrencyString(transaction.currency)} ${transaction.currency.name}"

                    val convertedAmountFormatted = convertedAmount?.let { amount ->
                        "${amount.toCurrencyString(selectedCurrency)} ${selectedCurrency.name}"
                    }

                    val exchangeRate = getExchangeRates(
                        transaction = transaction,
                        rates = rates,
                        selectedCurrency = selectedCurrency,
                        isFlipped = isFlipped
                    )

                    val summaryState = TransactionDetailSummaryState(
                        categoryIconRes = transaction.category.toCategoryUI().iconRes,
                        name = transaction.name,
                        type = transaction.type,
                        originalAmountWithCurrency = originalAmountFormatted,
                        originalCurrency = transaction.currency,
                        convertedAmountWithCurrency = convertedAmountFormatted,
                        convertedCurrency = selectedCurrency
                    )

                    val detailState = TransactionDetailState(
                        summary = summaryState,
                        category = transaction.category.toCategoryUI(),
                        dateText = transaction.date.toFullDateLongMonth(),
                        originalCurrency = transaction.currency,
                        convertedCurrency = selectedCurrency,
                        exchangeRate = exchangeRate,
                        notes = transaction.notes
                    )

                    UiState.Success(detailState)
                } else {
                    UiState.Failure(NoSuchElementException())
                }
            }.collect { uiState ->
                _state.update { currentState ->
                    currentState.copy(
                        detailState = uiState
                    )
                }
            }
        }
    }

    private fun getExchangeRates(
        transaction: Transaction,
        rates: ExchangeRates?,
        selectedCurrency: AppCurrency,
        isFlipped: Boolean
    ): String? {
        if (transaction.currency == selectedCurrency) {
            return null
        }

        val (fromCurrency, toCurrency) = if (!isFlipped) {
            transaction.currency to selectedCurrency
        } else {
            selectedCurrency to transaction.currency
        }

        val targetScale = toCurrency.decimalPlaces

        val unitRate = calculateAmount(
            amount = BigDecimal.ONE,
            sourceCurrency = fromCurrency,
            targetCurrency = toCurrency,
            rates = rates
        )

        val rateText = unitRate?.let { rate ->
            val rateScale = if (rate < BigDecimal.ONE) 6 else targetScale
            val formattedRate = if (rate < BigDecimal.ONE) {
                rate
                    .setScale(rateScale, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            } else {
                rate.toCurrencyString(toCurrency)
            }


            "1 ${fromCurrency.name} = $formattedRate ${toCurrency.name}"
        }

        return rateText
    }

    companion object {
        private const val VIEW_MODEL_TAG = "TransactionDetailViewModel"
    }
}