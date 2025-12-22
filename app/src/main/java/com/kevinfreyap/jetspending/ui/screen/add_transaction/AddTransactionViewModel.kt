package com.kevinfreyap.jetspending.ui.screen.add_transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.state.TransactionDraft
import com.kevinfreyap.jetspending.ui.state.TransactionState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.ErrorHelper
import com.kevinfreyap.jetspending.utils.combine
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    currencyUseCase: CurrencyUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
): ViewModel(){
    val currencyCode = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )

    // Draft
    private val _draftState = MutableStateFlow(TransactionDraft())

    // Name
    private val _transactionName = MutableStateFlow("")

    // Amount
    private val _transactionAmountInput = MutableStateFlow("")

    // Categories
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _categories = _draftState
        .map { it.transactionType }
        .distinctUntilChanged()
        .flatMapLatest { type ->
            categoryUseCase.getCategoryByType(type)
                .map { categories ->
                    categories.map { category ->
                        CategoryUiFormatter.mapCategoryDomainToUi(category)
                    }
                        .sortedBy { it.sortOrder }
                }
                .flowOn(Dispatchers.Default)
        }

    // Notes
    private val _transactionNotes = MutableStateFlow("")

    // Transaction State
    val transactionState: StateFlow<TransactionState> = combine(
        _draftState,
        _transactionName,
        _transactionAmountInput,
        _categories,
        currencyCode,
        _transactionNotes
    ) { draft, name, amount, categories, currency, notes ->

        val validCategory = categories.find { it.id == draft.transactionCategoryId }
        val dateDisplay = DateFormatter.formatToDateWithDay(draft.transactionDate)

        TransactionState(
            transactionName = name,
            transactionAmountInput = amount,
            transactionAmountDisplay = CurrencyUiFormatter.formatWithCode(draft.transactionAmountRaw, currency),
            transactionType = draft.transactionType,
            transactionCategories = categories,
            transactionCategoryId = validCategory?.id,
            transactionDate = draft.transactionDate,
            transactionDateDisplay = dateDisplay,
            transactionNotes = notes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionState()
    )

    // UiState
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Success
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog = _showSuccessDialog.asStateFlow()


    // Name
    fun onNameChange(name: String) {
        _transactionName.value = name

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_NAME)
    }

    // Amount
    fun initializeAmount() {
        val current = _draftState.value.transactionAmountRaw
        _transactionAmountInput.value = if (current == BigDecimal.ZERO) "" else current.toPlainString()
    }

    fun onAmountChange(amount: String) {
        val cleanAmount = CurrencyUiFormatter.cleanAmount(amount, currencyCode.value)

        if (cleanAmount == null) return

        _transactionAmountInput.value = cleanAmount

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_AMOUNT)
    }

    fun onSetAmount() {
        val trimmedFractionZero = CurrencyUiFormatter.trimFractionZero(_transactionAmountInput.value)

        _draftState.update {
            it.copy(transactionAmountRaw = trimmedFractionZero.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
    }

    // Type
    fun onSelectType(type: TransactionType) {
        _draftState.update {
            it.copy(transactionType = type, transactionCategoryId = null)
        }
    }

    /// Categories
    fun onSelectCategory(categoryId: String) {
        _draftState.update {
            it.copy(transactionCategoryId = categoryId)
        }

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_CATEGORY)
    }

    // Date
    fun onDateSelected(millis: Long?) {
        if (millis != null){
            _draftState.update {
                it.copy(transactionDate = Instant.ofEpochMilli(millis))
            }
        }
    }

    // Notes
    fun onNotesChange(notes: String) {
        _transactionNotes.value = notes
        if (notes.length <= 1000) {
            _uiState.value = ErrorHelper.removeError(_uiState.value, Field.TRANSACTION_NOTES)
        } else {
            _uiState.value = UiState.ValidationErrors(
                ErrorHelper.validationErrorsToUiError(
                    listOf(ValidationError.TransactionNotesTooLong)
                )
            )
        }
    }

    // Save Transaction
    fun onSaveTransaction() {
        _uiState.value = UiState.Loading
        val draft = _draftState.value

        val validationRes = validateTransaction(
            name = _transactionName.value,
            amount = draft.transactionAmountRaw,
            category = draft.transactionCategoryId,
            notes = _transactionNotes.value
        )

        if (validationRes.isNotEmpty()) {
            _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(validationRes))
            return
        }

        viewModelScope.launch {
            val result = transactionUseCase.insertTransaction(
                name = _transactionName.value,
                amount = draft.transactionAmountRaw,
                currency = currencyCode.value,
                type = draft.transactionType,
                categoryId = draft.transactionCategoryId ?: "",
                date = draft.transactionDate,
                stringDate = DateFormatter.formatToDailyRatesString(draft.transactionDate),
                notes = _transactionNotes.value
            )

            when(result) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showSuccessDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                }
                is DomainResult.Failure -> {
                    Log.e(VIEWMODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun validateTransaction(
        name: String,
        amount: BigDecimal,
        category: String?,
        notes: String
    ): List<ValidationError> {
        val currentErrors = mutableListOf<ValidationError>()

        if (name.isBlank()) currentErrors.add(ValidationError.TransactionNameRequired)
        if (amount <= BigDecimal.ZERO) currentErrors.add(ValidationError.TransactionAmountInvalid)
        if (category == null) currentErrors.add(ValidationError.TransactionCategoryMissing)
        if (notes.length > 1000) currentErrors.add(ValidationError.TransactionNotesTooLong)

        return currentErrors
    }

    fun onDialogDismissed() {
        _showSuccessDialog.value = false
    }

    companion object {
        private const val VIEWMODEL_TAG = "AddTransactionViewModel"
    }
}