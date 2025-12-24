package com.kevinfreyap.jetspending.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.BarChart
import com.kevinfreyap.jetspending.ui.components.CategoriesReport
import com.kevinfreyap.jetspending.ui.components.IncomeSpendingRow
import com.kevinfreyap.jetspending.ui.components.ViewDateSelector
import com.kevinfreyap.jetspending.ui.components.ViewPeriodSelector
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.main.MainViewModel
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.state.ReportState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ReportScreen (
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: ReportViewModel = hiltViewModel()
) {
    val selectedCurrency by mainViewModel.selectedCurrency.collectAsState()
    val reportState by viewModel.reportState.collectAsState()

    ReportContent(
        selectedCurrency = selectedCurrency,
        reportState = reportState,
        onSelectCurrency = {
            mainViewModel.onSelectCurrency(it)
        },
        onSelectPeriod = {
            viewModel.onSelectPeriod(it)
        },
        onNextClick = {
            viewModel.onNextClick()
        },
        onPreviousClick = {
            viewModel.onPreviousClick()
        },
        modifier = modifier
    )
}

@Composable
fun ReportContent(
    selectedCurrency: AppCurrency,
    reportState: UiState<ReportState>,
    onSelectCurrency: (AppCurrency) -> Unit,
    onSelectPeriod: (PeriodSelectorOption) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = reportState is UiState.Loading
    val data = if (reportState is UiState.Success) reportState.data else ReportState()

    Scaffold (
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.report),
                showActionButton = true,
                selectedCurrency = selectedCurrency,
                onSelectCurrency = onSelectCurrency
            )
        }
    ) { innerPadding ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            ViewPeriodSelector(
                selectedOption = data.selectedPeriod,
                onSelectOption = onSelectPeriod,
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            Card (
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = Theme.custom.cardColor
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                ViewDateSelector(
                    centerText = data.rangeLabel,
                    onPreviousClick = onPreviousClick,
                    onPreviousBtnEnabled = data.isPreviousEnabled,
                    onNextClick = onNextClick,
                    onNextBtnEnabled = data.isNextEnabled,
                    isLoading = isLoading,
                    modifier = Modifier
                        .fillMaxHeight()
                )
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            if (data.spendingIncomeBalanceUi.ratesIncomplete) {
                Text(
                    text = stringResource(R.string.error_some_transaction_rate_unavailable),
                    style = MaterialTheme.typography.labelMedium,
                    color = Red500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )
            }

            IncomeSpendingRow(
                spendingIncomeStats = data.spendingIncomeBalanceUi,
                isLoading = isLoading
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            BarChart()

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            CategoriesReport()
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ReportContentPreview() {
    JetSpendingTheme {
        ReportContent(
            selectedCurrency = AppCurrency.IDR,
            reportState = UiState.Success(
                ReportState(
                    selectedPeriod = PeriodSelectorOption.WEEKLY,
                    rangeLabel = "22 - 28 Dec 2025",
                    spendingIncomeBalanceUi = SpendingIncomeBalanceUi(
                        income = "Rp 1.000.000.000",
                        spending = "Rp 1.000.000",
                        ratesIncomplete = false
                    )
                )
            ),
            onSelectCurrency = {},
            onSelectPeriod = {},
            onNextClick = {},
            onPreviousClick = {}
        )
    }
}