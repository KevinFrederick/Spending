package com.kevinfreyap.jetspending.ui.screen.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.RecentTransactions
import com.kevinfreyap.jetspending.ui.components.SummaryInformation
import com.kevinfreyap.jetspending.ui.components.ViewDateSelector
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.main.MainViewModel
import com.kevinfreyap.jetspending.ui.model.DashboardUi
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.model.TotalBalanceUi
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant

@Composable
fun DashboardScreen(
    navigateToAddTransaction: () -> Unit,
    navigateToTransactionList: () -> Unit,
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val currencyCode by mainViewModel.selectedCurrency.collectAsState()
    val monthDisplay by viewModel.monthDisplay.collectAsState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val nextBtnEnabled by viewModel.nextBtnEnabled.collectAsState()
    val previousBtnEnabled by viewModel.previousBtnEnabled.collectAsState()

    DashboardContent(
        currencyCode = currencyCode,
        dashboardState = uiState,
        monthDisplay = monthDisplay,
        nextBtnEnabled = nextBtnEnabled,
        previousBtnEnabled = previousBtnEnabled,
        onSelectCurrency = {
            mainViewModel.onSelectCurrency(it)
        },
        onNextBtnClicked = {
            viewModel.onNextMonth()
        },
        onPreviousBtnClicked = {
            viewModel.onPreviousMonth()
        },
        navigateToAddTransaction = navigateToAddTransaction,
        navigateToTransactionList = navigateToTransactionList,
        navigateToDetail = navigateToDetail,
        onCheckRate = {
            mainViewModel.onRateMissing(it)
        },
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    currencyCode: AppCurrency,
    dashboardState: UiState<DashboardUi>,
    monthDisplay: String,
    nextBtnEnabled: Boolean,
    previousBtnEnabled: Boolean,
    onSelectCurrency: (AppCurrency) -> Unit,
    onNextBtnClicked: () -> Unit,
    onPreviousBtnClicked: () -> Unit,
    navigateToAddTransaction: () -> Unit,
    navigateToTransactionList: () -> Unit,
    navigateToDetail: (String) -> Unit,
    onCheckRate: (Instant) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = dashboardState is UiState.Loading
    val data = if (dashboardState is UiState.Success) dashboardState.data else DashboardUi()

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.dashboard),
                selectedCurrency = currencyCode,
                onSelectCurrency = onSelectCurrency,
                showActionButton = true,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddTransaction,
                containerColor = Blue500,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_24),
                    contentDescription = stringResource(R.string.add_transaction),
                )
            }
        },
    ) { innerPadding ->
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
            SummaryInformation(
                totalBalance = data.totalBalance.balance,
                monthlyBalanceUi = data.monthlyBalance,
                isIncomplete = data.totalBalance.isIncomplete,
                isLoading = isLoading,
                dateSelectorSlot = {
                    Card (
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = Theme.custom.cardColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        ViewDateSelector(
                            centerText = monthDisplay,
                            onPreviousClick = onPreviousBtnClicked,
                            onPreviousBtnEnabled = previousBtnEnabled,
                            onNextClick = onNextBtnClicked,
                            onNextBtnEnabled = nextBtnEnabled,
                            isLoading = isLoading,
                            modifier = Modifier
                                .fillMaxHeight()
                        )
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            RecentTransactions(
                transactions = data.latestTransactions,
                navigateToTransactionList = navigateToTransactionList,
                navigateToDetail = navigateToDetail,
                onCheckRate = onCheckRate,
                isLoading = isLoading
            )

            Spacer(
                modifier = Modifier.height(32.dp)
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
fun DashboardContentPreview(){
    JetSpendingTheme {
        DashboardContent(
            currencyCode = AppCurrency.IDR,
            dashboardState = UiState.Success(
                DashboardUi(
                    totalBalance = TotalBalanceUi(
                        balance = "Rp 100.000",
                        isIncomplete = false
                    ),
                    monthlyBalance = SpendingIncomeBalanceUi(
                        income = "Rp 1.000.000",
                        spending = "Rp 500.000"
                    ),
                    latestTransactions = listOf(
                        TransactionItemUi(
                            transactionId = "1",
                            transactionName = "Salary",
                            transactionAmount = "+ Rp 1.000.000",
                            transactionDate = "24 November 2025",
                            transactionDateRaw = Instant.now(),
                            transactionTypeBackground = Green500,
                            transactionCategoryIcon = R.drawable.ic_salary_icon,
                            isConversionPending = false
                        ),
                        TransactionItemUi(
                            transactionId = "2",
                            transactionName = "Salary",
                            transactionAmount = "+ Rp 1.000.000",
                            transactionDate = "24 November 2025",
                            transactionDateRaw = Instant.now(),
                            transactionTypeBackground = Green500,
                            transactionCategoryIcon = R.drawable.ic_salary_icon,
                            isConversionPending = false
                        ),
                        TransactionItemUi(
                            transactionId = "3",
                            transactionName = "Salary",
                            transactionAmount = "+ Rp 1.000.000",
                            transactionDate = "24 November 2025",
                            transactionDateRaw = Instant.now(),
                            transactionTypeBackground = Green500,
                            transactionCategoryIcon = R.drawable.ic_salary_icon,
                            isConversionPending = false
                        )
                    )
                )
            ),
            monthDisplay = "December 2025",
            onSelectCurrency = {},
            navigateToAddTransaction = {},
            navigateToTransactionList = {},
            navigateToDetail = {},
            onCheckRate = {},
            nextBtnEnabled = false,
            previousBtnEnabled = true,
            onNextBtnClicked = {  },
            onPreviousBtnClicked = {},
        )
    }
}