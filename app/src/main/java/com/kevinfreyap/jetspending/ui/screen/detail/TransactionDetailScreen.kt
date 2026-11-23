package com.kevinfreyap.jetspending.ui.screen.detail

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.TransactionDetailAction
import com.kevinfreyap.jetspending.ui.components.v2.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.v2.ViewAppDialog
import com.kevinfreyap.jetspending.ui.components.v2.ViewPageStatus
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.model.DialogType
import com.kevinfreyap.jetspending.ui.navigation.TransactionDetailNavigation
import com.kevinfreyap.jetspending.ui.screen.detail.components.TransactionDetailSectionPlaceholder
import com.kevinfreyap.jetspending.ui.screen.detail.components.TransactionDetailSummary
import com.kevinfreyap.jetspending.ui.screen.detail.components.TransactionDetailSummaryPlaceholder
import com.kevinfreyap.jetspending.ui.screen.detail.components.TransactionDetailsSection
import com.kevinfreyap.jetspending.ui.screen.detail.state.TransactionDetailSummaryState
import com.kevinfreyap.jetspending.ui.state.TransactionDetailState
import com.kevinfreyap.jetspending.ui.state.TransactionDetailUIState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.UiEvent

@Composable
fun TransactionDetailScreen(
    onNavigate: (TransactionDetailNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Navigate -> {
                        onNavigate(event.destination)
                    }
                    is UiEvent.ShowToast -> {
                        Toast.makeText(
                            context,
                            event.asString(context),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    TransactionDetailContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun TransactionDetailContent(
    state: TransactionDetailUIState,
    onAction: (TransactionDetailAction) -> Unit,
    onNavigate: (TransactionDetailNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.transaction_detail),
                onBackClick = {
                    onNavigate(
                        TransactionDetailNavigation.NavigateUp
                    )
                },
                isLoading = state.detailState is UiState.Loading || state.isDeleting,
            )
        },
        bottomBar = {
            if (state.detailState !is UiState.Failure) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 32.dp
                        )
                        .navigationBarsPadding()
                ) {
                    ViewPrimaryButton(
                        text = stringResource(R.string.btn_edit_transaction),
                        onClick = {
                            state.transactionId?.let { transactionId ->
                                onNavigate(
                                    TransactionDetailNavigation.NavigateToEditTransaction(
                                        transactionId = transactionId
                                    )
                                )
                            }
                        },
                        enabled = state.detailState is UiState.Success,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit_24),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    ViewPrimaryButton(
                        text = stringResource(R.string.btn_delete_transaction),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = Theme.custom.statusNegative,
                        onClick = {
                            onAction(
                                TransactionDetailAction.OnToggleDeleteDialog(
                                    isVisible = true
                                )
                            )
                        },
                        enabled = state.detailState is UiState.Success,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_24),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 12.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when(state.detailState) {
                UiState.Idle -> {}
                UiState.Loading -> TransactionDetailPlaceholder()
                is UiState.Success -> TransactionDetailSuccess(
                    state = state.detailState.data,
                    onAction = onAction
                )
                is UiState.ValidationErrors -> {}
                is UiState.Failure -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        TransactionDetailError(
                            onNavigateBack = {
                                onNavigate(
                                    TransactionDetailNavigation.NavigateUp
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.isDeleteDialogVisible) {
        ViewAppDialog(
            dialogType = DialogType.DESTRUCTIVE,
            title = stringResource(R.string.dialog_delete_transaction),
            message = stringResource(R.string.description_delete_transaction),
            confirmText = stringResource(R.string.btn_delete),
            onConfirm = {
                onAction(
                    TransactionDetailAction.OnConfirmDelete
                )
            },
            dismissText = stringResource(R.string.cancel),
            onDismissRequest = {
                onAction(
                    TransactionDetailAction.OnToggleDeleteDialog(
                        isVisible = false
                    )
                )
            }
        )
    }
}

@Composable
private fun TransactionDetailPlaceholder() {
    TransactionDetailSummaryPlaceholder()
    TransactionDetailSectionPlaceholder()
}

@Composable
private fun TransactionDetailSuccess(
    state: TransactionDetailState,
    onAction: (TransactionDetailAction) -> Unit
) {
    TransactionDetailSummary(
        state = state.summary
    )

    TransactionDetailsSection(
        transaction = state,
        onAction = onAction
    )
}

@Composable
private fun TransactionDetailError(
    onNavigateBack: () -> Unit
) {
    ViewPageStatus(
        title = stringResource(R.string.error_transaction_not_found),
        message = stringResource(R.string.description_transaction_missing),
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_priority_high_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        },
        actionBtn = {
            ViewPrimaryButton(
                text = stringResource(R.string.btn_back),
                onClick = onNavigateBack,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = null,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            )
        }
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TransactionDetailPreview() {
    JetSpendingTheme {
        TransactionDetailContent(
            state = TransactionDetailUIState(
                detailState = UiState.Success(
                    TransactionDetailState(
                        summary = TransactionDetailSummaryState(
                            categoryIconRes = R.drawable.ic_food_icon,
                            name = "Lunch",
                            type = TransactionType.SPENDING,
                            originalAmountWithCurrency = "20.000 IDR",
                            originalCurrency = AppCurrency.IDR,
                            convertedAmountWithCurrency = "4.48 MYR",
                            convertedCurrency = AppCurrency.MYR
                        ),
                        category = CategoryUI(
                            id = "1",
                            sortOrder = 1,
                            name = R.string.category_food,
                            iconRes = R.drawable.ic_food_icon,
                            types = listOf(TransactionType.SPENDING)
                        ),
                        dateText = "17 November 2026",
                        originalCurrency = AppCurrency.IDR,
                        convertedCurrency = AppCurrency.USD,
                        exchangeRate = "1 IDR = 0.000224",
                        notes = ""
                    )
                ),
                isDeleteDialogVisible = false
            ),
            onAction = {},
            onNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DetailTransactionPreview_Loading() {
    JetSpendingTheme {
        TransactionDetailContent(
            state = TransactionDetailUIState(
                detailState = UiState.Loading
            ),
            onAction = {},
            onNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DetailTransactionPreview_Error() {
    JetSpendingTheme {
        TransactionDetailContent(
            state = TransactionDetailUIState(
                detailState = UiState.Failure(IllegalStateException())
            ),
            onAction = {},
            onNavigate = {}
        )
    }
}