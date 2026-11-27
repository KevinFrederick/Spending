package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.helper.date.toFullDateLongMonth
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.transaction_form.state.AmountEntryState
import com.kevinfreyap.jetspending.ui.screen.transaction_form.state.TransactionOverlayState
import java.time.Instant

data class TransactionFormState(
    val isEditMode: Boolean = false,
    val initialSnapshot: TransactionFormState? = null,
    val isInitialLoading: Boolean = true,
    val categories: List<CategoryUI> = emptyList(),
    val amountEntry: AmountEntryState = AmountEntryState(),

    val transactionType: TransactionType = TransactionType.SPENDING,
    val selectedCategory: CategoryUI? = null,
    val name: String = "",
    val notes: String? = null,
    val date: Instant = Instant.now(),
    val overlayState: TransactionOverlayState = TransactionOverlayState(),
    val uiState: UiState<Unit> = UiState.Idle
) {
    val formattedDate: String
        get() = date.toFullDateLongMonth()

    val hasUnsavedChanges: Boolean
        get() = if (initialSnapshot != null) {
            amountEntry.originalAmount != initialSnapshot.amountEntry.originalAmount ||
            amountEntry.originalCurrency != initialSnapshot.amountEntry.originalCurrency ||
            transactionType != initialSnapshot.transactionType ||
            selectedCategory?.id != initialSnapshot.selectedCategory?.id ||
            name != initialSnapshot.name ||
            notes != initialSnapshot.notes ||
            date != initialSnapshot.date
        } else {
            amountEntry.originalAmount.isNotBlank() ||
            selectedCategory != null ||
            name.isNotBlank() ||
            !notes.isNullOrBlank()
        }
}
