package com.kevinfreyap.jetspending.ui.screen.transaction_form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.ErrorReporter
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.category.GetCategoryByTypeUseCase
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.currency.GetExchangeRatesByDateUseCase
import com.kevinfreyap.domain.usecase.transaction.GetTransactionByIdUseCase
import com.kevinfreyap.domain.usecase.transaction.InsertTransactionUseCase
import com.kevinfreyap.domain.usecase.transaction.UpdateTransactionUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.TransactionFormAction
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.helper.currency.cleanStringAmount
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.date.toDateKey
import com.kevinfreyap.jetspending.ui.helper.error.removeError
import com.kevinfreyap.jetspending.ui.helper.error.toUIError
import com.kevinfreyap.jetspending.ui.navigation.TransactionFormNavigation
import com.kevinfreyap.jetspending.ui.screen.transaction_form.state.AmountEntryState
import com.kevinfreyap.jetspending.ui.state.TransactionFormState
import com.kevinfreyap.jetspending.ui.state.UiEvent
import com.kevinfreyap.jetspending.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrency: GetCurrencyUseCase,
    private val getCategoryByType: GetCategoryByTypeUseCase,
    private val getExchangeRatesByDate: GetExchangeRatesByDateUseCase,
    private val calculateAmountBasedOnRates: CalculateAmountBasedOnRatesUseCase,
    private val getTransactionById: GetTransactionByIdUseCase,
    private val insertTransaction: InsertTransactionUseCase,
    private val updateTransaction: UpdateTransactionUseCase,
    private val errorReporter: ErrorReporter
): ViewModel() {
    private val _uiEvent = Channel<UiEvent<TransactionFormNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(TransactionFormState())
    val state: StateFlow<TransactionFormState> = _state.asStateFlow()

    val transactionId: String? = savedStateHandle["transactionId"]
    val isEditMode: Boolean = !transactionId.isNullOrBlank()

    private var cachedRates: ExchangeRates? = null
    private var rateFetchJob: Job? = null

    init {
        loadInitialData()
    }

    fun onAction(action: TransactionFormAction) {
        when(action) {
            is TransactionFormAction.TransactionOverlayAction -> handleOverlayAction(action)
            is TransactionFormAction.TransactionMainAction -> handleMainAction(action)
            is TransactionFormAction.SubmitTransaction -> submitTransaction()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val currency = getCurrency().first()
            if (isEditMode && transactionId != null) {
                val existing = getTransactionById(transactionId).first()
                if (existing != null) {
                    val transaction = existing.transaction
                    val categories = getCategoryByType(transaction.type)
                        .first()
                        .map { it.toCategoryUI() }

                    cachedRates = existing.rates ?: getExchangeRatesByDate(transaction.date)

                    val loadedAmountEntry = recalculateAmountEntry(
                        AmountEntryState(
                            originalAmount = transaction.amount.toPlainString(),
                            originalCurrency = transaction.currency,
                            convertedCurrency = currency,
                            rateDate = transaction.date
                        )
                    )

                    val loadedState = TransactionFormState(
                        isEditMode = true,
                        isInitialLoading = false,
                        transactionType = transaction.type,
                        selectedCategory = transaction.category.toCategoryUI(),
                        name = transaction.name,
                        notes = transaction.notes,
                        date = transaction.date,
                        categories = categories,
                        amountEntry = loadedAmountEntry
                    )

                    _state.update {
                        loadedState.copy(
                            initialSnapshot = loadedState
                        )
                    }
                } else {
                    _uiEvent.send(UiEvent.ShowToast(R.string.error_transaction_not_found))
                    _uiEvent.send(UiEvent.Navigate(TransactionFormNavigation.NavigateUp))
                }
            } else {
                val categories = getCategoryByType(_state.value.transactionType)
                    .first()
                    .map { category ->
                        category.toCategoryUI()
                    }
                val initialDate = _state.value.date
                cachedRates = getExchangeRatesByDate(initialDate)

                _state.update { currentState ->
                    val updatedAmountEntry = recalculateAmountEntry(
                        currentState.amountEntry.copy(
                            originalCurrency = currency,
                            convertedCurrency = currency
                        )
                    )

                    currentState.copy(
                        isEditMode = false,
                        isInitialLoading = false,
                        categories = categories,
                        amountEntry = updatedAmountEntry
                    )
                }
            }
        }
    }

    private fun loadCategories(type: TransactionType) {
        viewModelScope.launch {
            val categories = getCategoryByType(type)
                .map { categories ->
                    categories.map { it.toCategoryUI() }
                }
                .first()

            _state.update { currentState ->
                val isCategoryValid = categories.any { it.id == currentState.selectedCategory?.id }
                currentState.copy(
                    categories = categories,
                    selectedCategory = if (isCategoryValid) currentState.selectedCategory else null
                )
            }
        }
    }

    private fun loadExchangeRates(date: Instant) {
        rateFetchJob?.cancel()
        rateFetchJob = viewModelScope.launch {
            cachedRates = getExchangeRatesByDate(date)

            _state.update { currentState ->
                currentState.copy(
                    amountEntry = recalculateAmountEntry(
                        currentState.amountEntry.copy(
                            rateDate = date
                        )
                    )
                )
            }
        }
    }

    private fun recalculateAmountEntry(entry: AmountEntryState): AmountEntryState {
        if (entry.originalCurrency == entry.convertedCurrency) {
            return entry.copy(
                convertedAmount = entry.originalAmount,
                conversionRate = null
            )
        }

        val targetScale = entry.convertedCurrency.decimalPlaces

        val unitRate = calculateAmountBasedOnRates(
            amount = BigDecimal.ONE,
            sourceCurrency = entry.originalCurrency,
            targetCurrency = entry.convertedCurrency,
            rates = cachedRates
        )

        val rateText = unitRate?.let { rate ->
            val rateScale = if (rate < BigDecimal.ONE) 6 else targetScale
            val formattedRate = if (rate < BigDecimal.ONE) {
                rate
                    .setScale(rateScale, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            } else {
                rate.toCurrencyString(entry.convertedCurrency)
            }

            "1 ${entry.originalCurrency.name} = $formattedRate ${entry.convertedCurrency.name}"
        }

        val rawNumeric = entry.originalAmount.toBigDecimalOrNull()
        val converted = if (rawNumeric != null) {
            calculateAmountBasedOnRates(
                amount = rawNumeric,
                sourceCurrency = entry.originalCurrency,
                targetCurrency = entry.convertedCurrency,
                rates = cachedRates
            )?.setScale(targetScale, RoundingMode.HALF_UP)
                ?.toPlainString()
                .orEmpty()
        } else {
            ""
        }

        return entry.copy(
            convertedAmount = converted,
            conversionRate = rateText
        )
    }

    fun submitTransaction() {
        if (_state.value.uiState is UiState.Loading) return

        _state.update {
            it.copy(uiState = UiState.Loading)
        }

        val currentState = _state.value

        viewModelScope.launch {
            val amount = currentState.amountEntry.originalAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO

            val result = if (isEditMode && transactionId != null) {
                updateTransaction(
                    id = transactionId,
                    name = currentState.name,
                    amount = amount,
                    currency = currentState.amountEntry.originalCurrency,
                    type = currentState.transactionType,
                    categoryId = currentState.selectedCategory?.id.orEmpty(),
                    date = currentState.date,
                    stringDate = currentState.date.toDateKey(),
                    notes = currentState.notes.orEmpty()
                )
            } else {
                insertTransaction(
                    name = currentState.name,
                    amount = amount,
                    currency = currentState.amountEntry.originalCurrency,
                    type = currentState.transactionType,
                    categoryId = currentState.selectedCategory?.id.orEmpty(),
                    date = currentState.date,
                    stringDate = currentState.date.toDateKey(),
                    notes = currentState.notes.orEmpty()
                )

            }

            when (result) {
                is DomainResult.Success -> {
                    _state.update {
                        it.copy(
                            uiState = UiState.Success(Unit)
                        )
                    }

                    val successMessageRes = if (isEditMode) {
                        R.string.success_message_transaction_updated
                    } else {
                        R.string.success_message_transaction_saved
                    }
                    _uiEvent.send(UiEvent.ShowToast(successMessageRes))
                    _uiEvent.send(UiEvent.Navigate(TransactionFormNavigation.NavigateUp))
                }
                is DomainResult.ValidationFailed -> {
                    _state.update {
                        it.copy(
                            uiState = UiState.ValidationErrors(result.errors.toUIError())
                        )
                    }
                }
                is DomainResult.Failure -> {
                    errorReporter.recordException(result.throwable)
                    _state.update { currentState ->
                        currentState.copy(
                            uiState = UiState.Failure(result.throwable)
                        )
                    }
                }
            }
        }
    }

    private fun handleOverlayAction(action: TransactionFormAction.TransactionOverlayAction) {
        when (action) {
            is TransactionFormAction.TransactionOverlayAction.OnToggleCategoryBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        overlayState = currentState.overlayState.copy(
                            isCategorySheetVisible = action.isVisible
                        )
                    )
                }
            }
            is TransactionFormAction.TransactionOverlayAction.OnToggleDateDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        overlayState = currentState.overlayState.copy(
                            isDateDialogVisible = action.isVisible
                        )
                    )
                }
            }
            is TransactionFormAction.TransactionOverlayAction.OnToggleDiscardDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        overlayState = currentState.overlayState.copy(
                            isDiscardDialogVisible = action.isVisible
                        )
                    )
                }
            }
            is TransactionFormAction.TransactionOverlayAction.OnConfirmDiscard -> {
                _state.update { currentState ->
                    currentState.copy(
                        overlayState = currentState.overlayState.copy(
                            isDiscardDialogVisible = false
                        )
                    )
                }
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(TransactionFormNavigation.NavigateUp))
                }
            }
        }
    }

    private fun handleMainAction(action: TransactionFormAction.TransactionMainAction) {
        when(action) {
            is TransactionFormAction.TransactionMainAction.OnSelectTransactionType -> {
                _state.update { currentState ->
                    currentState.copy(
                        transactionType = action.type,
                    )
                }

                loadCategories(action.type)
            }
            is TransactionFormAction.TransactionMainAction.OnAmountChange -> {
                _state.update { currentState ->
                    val cleanAmount = action.amount.cleanStringAmount(_state.value.amountEntry.originalCurrency) ?: return

                    val updatedAmountEntry = recalculateAmountEntry(
                        currentState.amountEntry.copy(
                            originalAmount = cleanAmount
                        )
                    )

                    currentState.copy(
                        amountEntry = updatedAmountEntry,
                        uiState = currentState.uiState.removeError(Field.TRANSACTION_AMOUNT)
                    )
                }
            }
            is TransactionFormAction.TransactionMainAction.OnCurrencySelected -> {
                _state.update { currentState ->
                    val updatedAmountEntry = recalculateAmountEntry(
                        currentState.amountEntry.copy(
                            originalCurrency = action.currency
                        )
                    )

                    currentState.copy(
                        amountEntry = updatedAmountEntry
                    )
                }
            }
            is TransactionFormAction.TransactionMainAction.OnCategorySelected -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedCategory = action.category,
                        uiState = currentState.uiState.removeError(Field.TRANSACTION_CATEGORY)
                    )
                }
            }
            is TransactionFormAction.TransactionMainAction.OnNameChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        name = action.name,
                        uiState = currentState.uiState.removeError(Field.TRANSACTION_NAME)
                    )
                }
            }
            is TransactionFormAction.TransactionMainAction.OnNotesChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        notes = action.notes,
                        uiState = currentState.uiState.removeError(Field.TRANSACTION_NOTES)
                    )
                }
            }
            is TransactionFormAction.TransactionMainAction.OnDateSelected -> {
                val newDate = Instant.ofEpochMilli(action.date)
                _state.update { currentState ->
                    currentState.copy(
                        date = newDate
                    )
                }
                loadExchangeRates(newDate)
            }
        }
    }
}
