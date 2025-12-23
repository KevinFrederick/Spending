package com.kevinfreyap.jetspending.ui.screen.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewCategoryPill
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewDropdownCurrency
import com.kevinfreyap.jetspending.ui.components.ViewNotesInput
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.state.TransactionDetailState
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme
import kotlinx.coroutines.delay

@Composable
fun DetailTransactionScreen(
    transactionId: String?,
    onBackClick: () -> Unit,
    navigateToUpdate: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailTransactionViewModel = hiltViewModel()
) {
    transactionId?.let { viewModel.onSetTransactionId(it) }

    val transactionState by viewModel.transactionState.collectAsState()

    val showDeleteSuccessDialog by viewModel.showDeleteSuccessDialog.collectAsState()
    val isDelete by viewModel.isDelete.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailTransactionContent(
        transactionState = transactionState,
        showDeleteDialog = showDeleteDialog,
        showDeleteSuccessDialog = showDeleteSuccessDialog,
        isDeleting = isDelete,
        onShowDeleteDialog = {
            showDeleteDialog = it
        },
        onPositiveDialogClicked = {
            showDeleteDialog = false
            viewModel.onDeleteTransaction()
        },
        onSelectCurrency = {
            viewModel.onSelectCurrency(it)
        },
        onDismissDeleteSuccessDialog = {
            viewModel.onDismissSuccessDialog()
            onBackClick()
        },
        navigateToUpdate = {
            navigateToUpdate (transactionId)
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransactionContent(
    transactionState: TransactionDetailState?,
    showDeleteDialog: Boolean,
    showDeleteSuccessDialog: Boolean,
    isDeleting: Boolean,
    onShowDeleteDialog: (Boolean) -> Unit,
    onPositiveDialogClicked: () -> Unit,
    onSelectCurrency: (AppCurrency) -> Unit,
    onDismissDeleteSuccessDialog: () -> Unit,
    navigateToUpdate: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.transaction_detail),
                        color = Theme.custom.textColor,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onShowDeleteDialog(true)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete_24),
                            contentDescription = stringResource(R.string.delete_transaction),
                            tint = Theme.custom.iconColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back_24),
                            tint = Theme.custom.iconColor,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                windowInsets = WindowInsets(top = 0.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToUpdate,
                containerColor = Blue500,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mode_edit_24),
                    contentDescription = stringResource(R.string.edit_transaction),
                )
            }
        }
    ) { innerPadding ->
        if (transactionState == null && !isDeleting) {
            LaunchedEffect(Unit) {
                delay(2000)
                onBackClick()
            }

            ViewCustomDialog(
                onDismissRequest = onBackClick,
                icon = R.drawable.ic_error_outline_24,
                iconColor = Red500,
                title = stringResource(R.string.error_transaction_missing),
                message = stringResource(R.string.description_transaction_missing)
            )
        } else if (!isDeleting && transactionState != null){
            val typeText = when (transactionState.transactionType) {
                TransactionType.INCOME -> stringResource(R.string.income)
                TransactionType.SPENDING -> stringResource(R.string.spending)
                null -> ""
            }
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(
                        top = 8.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(
                                Alignment.Center
                            )
                            .padding(
                                top = 36.dp,
                                bottom = 24.dp
                            )
                    ) {
                        Text(
                            text = transactionState.transactionName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Theme.custom.textColor,
                        )

                        Spacer(
                            modifier = Modifier
                                .height(4.dp)
                        )

                        Text(
                            text = transactionState.transactionAmountDisplay,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.displaySmall,
                            color = transactionState.transactionColor,
                        )

                        Spacer(
                            modifier = Modifier
                                .height(4.dp)
                        )

                        Text(
                            text = transactionState.transactionDateDisplay,
                            style = MaterialTheme.typography.titleMedium,
                            color = Theme.custom.textColor,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = Theme.custom.cardColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.currency),
                            style = MaterialTheme.typography.titleMedium,
                            color = Theme.custom.textColor,
                        )

                        transactionState.transactionCurrency?.let { currency ->
                            ViewDropdownCurrency(
                                selectedCurrency = currency,
                                onSelectCurrency = onSelectCurrency
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = Theme.custom.cardColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.type),
                            style = MaterialTheme.typography.titleMedium,
                            color = Theme.custom.textColor,
                        )

                        Text(
                            text = typeText,
                            color = Theme.custom.textColor,
                            modifier = Modifier
                                .padding(end = 16.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = Theme.custom.cardColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp,
                                start = 16.dp,
                                bottom = 8.dp,
                                end = 8.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.category),
                            style = MaterialTheme.typography.titleMedium,
                            color = Theme.custom.textColor,
                        )

                        if (transactionState.transactionCategory != null) {
                            ViewCategoryPill(
                                name = stringResource(transactionState.transactionCategory.name),
                                iconRes = transactionState.transactionCategory.iconRes,
                                bgColor = transactionState.transactionColor
                            )
                        }

                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = Theme.custom.cardColor
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.transaction_notes),
                            style = MaterialTheme.typography.titleMedium,
                            color = Theme.custom.textColor
                        )

                        ViewNotesInput(
                            value = transactionState.transactionNotes,
                            onValueChange = {},
                            readOnly = true,
                        )
                    }
                }
            }
        }


        if (showDeleteDialog) {
            ViewCustomDialog(
                onDismissRequest = {
                    onShowDeleteDialog(false)
                },
                title = stringResource(R.string.delete_transaction),
                message = stringResource(R.string.description_delete_transaction),
                icon = R.drawable.ic_error_outline_24,
                iconColor = Red500,
                positiveBtn = {
                    Button(
                        onClick = onPositiveDialogClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red500
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.confirm)
                        )
                    }
                },
                negativeBtn = {
                    Button(
                        onClick = {
                            onShowDeleteDialog(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Grey500
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.cancel)
                        )
                    }
                }
            )
        }

        if (showDeleteSuccessDialog) {
            LaunchedEffect(Unit) {
                delay(2000)
                onDismissDeleteSuccessDialog()
            }

            ViewCustomDialog(
                onDismissRequest = onDismissDeleteSuccessDialog,
                icon = R.drawable.ic_check_24,
                iconColor = Orange700,
                title = stringResource(R.string.transaction_deleted),
                message = stringResource(R.string.description_delete_transaction_success)
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DetailTransactionPreview() {
    JetSpendingTheme {
        DetailTransactionContent(
            transactionState = TransactionDetailState(
                transactionName = "Salary Deposit",
                transactionAmountDisplay = "+ Rp 2.500.000",
                transactionType = TransactionType.INCOME,
                transactionDateDisplay = "Monday, 22 December 2025 | 17:10",
                transactionCategory = CategoryUI(
                    id = "CAT-SALARY",
                    sortOrder = 1,
                    name = R.string.category_salary,
                    iconRes = R.drawable.ic_salary_icon
                ),
                transactionColor = Green500,
                transactionCurrency = AppCurrency.IDR
            ),
            isDeleting = false,
            showDeleteSuccessDialog = false,
            showDeleteDialog = false,
            onShowDeleteDialog = { },
            onPositiveDialogClicked = {},
            onSelectCurrency = { },
            onBackClick = { },
            navigateToUpdate = { },
            modifier = Modifier,
            onDismissDeleteSuccessDialog = {}
        )
    }
}