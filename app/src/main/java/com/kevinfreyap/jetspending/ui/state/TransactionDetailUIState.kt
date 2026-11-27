package com.kevinfreyap.jetspending.ui.state

data class TransactionDetailUIState(
    val transactionId: String? = null,
    val detailState: UiState<TransactionDetailState> = UiState.Idle,
    val isDeleteDialogVisible: Boolean = false,
    val isDeleting: Boolean = false
)
