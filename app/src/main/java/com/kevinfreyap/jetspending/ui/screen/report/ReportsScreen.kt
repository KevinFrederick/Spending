package com.kevinfreyap.jetspending.ui.screen.report

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.ReportsAction
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewDropdownSelection
import com.kevinfreyap.jetspending.ui.components.ViewMonthYearPickerDialog
import com.kevinfreyap.jetspending.ui.components.ViewPageStatus
import com.kevinfreyap.jetspending.ui.components.ViewPeriodNavigator
import com.kevinfreyap.jetspending.ui.components.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.ViewWeekPickerDialog
import com.kevinfreyap.jetspending.ui.helper.other.toLocalePercentageText
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.report.components.BarChartCard
import com.kevinfreyap.jetspending.ui.screen.report.components.BarChartCardPlaceholder
import com.kevinfreyap.jetspending.ui.screen.report.components.CategoryListSection
import com.kevinfreyap.jetspending.ui.screen.report.components.CategoryListSectionPlaceholder
import com.kevinfreyap.jetspending.ui.screen.report.components.TotalSummarySection
import com.kevinfreyap.jetspending.ui.screen.report.components.TotalSummarySectionPlaceholder
import com.kevinfreyap.jetspending.ui.screen.report.components.WeeklySummaryBanner
import com.kevinfreyap.jetspending.ui.screen.report.state.CategoryPercentageItemState
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartBarItemState
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartUiState
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportTotalSummaryState
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportsContentState
import com.kevinfreyap.jetspending.ui.screen.report.state.WeeklySummaryBannerState
import com.kevinfreyap.jetspending.ui.state.ReportsState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.state.UiText
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import java.time.LocalDate

@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReportsContent(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
private fun ReportsContent(
    state: ReportsState,
    onAction: (ReportsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.screen_report),
                actionButton = {
                    ViewDropdownSelection(
                        options = PeriodSelectorOption.entries,
                        selectedOption = state.queryFilter.selectedPeriod,
                        onSelectOption = { period ->
                            onAction(
                                ReportsAction.OnPeriodSelected(
                                    period = period
                                )
                            )
                        },
                        optionLabel = {
                            stringResource(
                                when(it) {
                                    PeriodSelectorOption.WEEKLY -> R.string.period_weekly
                                    PeriodSelectorOption.MONTHLY -> R.string.period_monthly
                                    PeriodSelectorOption.YEARLY -> R.string.period_yearly
                                }
                            )
                        },
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                },
                isLoading = state.content is UiState.Loading
            )
        }
    ) {innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 4.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onAction(
                                ReportsAction.OnClearSelectedBar
                            )
                        }
                    )
                }
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(
                visible = state.weeklyBanner != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                state.weeklyBanner?.let { bannerState ->
                    WeeklySummaryBanner(
                        state = bannerState,
                        onDismiss = {
                            onAction(ReportsAction.OnDismissWeeklyBanner)
                        }
                    )
                }
            }

            ViewPeriodNavigator(
                label = state.periodLabel,
                onPreviousClick = {
                    onAction(
                        ReportsAction.OnPreviousPeriod
                    )
                },
                onNextClick = {
                    onAction(
                        ReportsAction.OnNextPeriod
                    )
                },
                canGoNext = state.canGoNext,
                onLabelClick = {
                    onAction(
                        ReportsAction.OnShowDateDialog(
                            isVisible = true
                        )
                    )
                }
            )

            when(
                val content = state.content
            ) {
                UiState.Idle -> {}
                UiState.Loading -> ReportsLoading()
                is UiState.Success -> ReportsSuccess(
                    state = content.data,
                    selectedType = state.queryFilter.selectedType,
                    onAction = onAction
                )
                is UiState.ValidationErrors -> {}
                is UiState.Failure -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        ReportsError(
                            throwable = content.throwable,
                            onRetry = {
                                onAction(
                                    ReportsAction.OnPeriodSelected(state.queryFilter.selectedPeriod)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.queryFilter.isDateDialogVisible) {
        val period = state.queryFilter.selectedPeriod
        val anchorDate = state.queryFilter.anchorDate

        when(period) {
            PeriodSelectorOption.WEEKLY -> {
                ViewWeekPickerDialog(
                    initialDate = anchorDate,
                    onWeekSelected = { mondayDate ->
                        onAction(
                            ReportsAction.OnSelectDate(
                                date = mondayDate
                            )
                        )
                    },
                    onDismiss = {
                        onAction(
                            ReportsAction.OnShowDateDialog(
                                isVisible = false
                            )
                        )
                    },
                    onReset = {
                        onAction(
                            ReportsAction.OnSelectDate(
                                date = LocalDate.now()
                            )
                        )
                    }
                )
            }
            PeriodSelectorOption.MONTHLY -> {
                ViewMonthYearPickerDialog(
                    initialDate = anchorDate,
                    isYearOnly = false,
                    onMonthYearSelected = { month, year ->
                        val newDate = LocalDate.of(year, month, 1)

                        onAction(
                            ReportsAction.OnSelectDate(
                                date = newDate
                            )
                        )
                    },
                    onDismiss = {
                        onAction(
                            ReportsAction.OnShowDateDialog(
                                isVisible = false
                            )
                        )
                    },
                    onReset = {
                        onAction(
                            ReportsAction.OnSelectDate(
                                date = LocalDate.now()
                            )
                        )
                    }
                )
            }
            PeriodSelectorOption.YEARLY -> {
                ViewMonthYearPickerDialog(
                    initialDate = anchorDate,
                    isYearOnly = true,
                    onYearSelected = { year ->
                        val newDate = LocalDate.of(year, 1, 1)

                        onAction(
                            ReportsAction.OnSelectDate(
                                date = newDate
                            )
                        )
                    },
                    onDismiss = {
                        onAction(
                            ReportsAction.OnShowDateDialog(
                                isVisible = false
                            )
                        )
                    },
                    onReset = {
                        onAction(
                            ReportsAction.OnSelectDate(
                                date = LocalDate.now()
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ReportsSuccess(
    state: ReportsContentState,
    selectedType: TransactionType,
    onAction: (ReportsAction) -> Unit,
) {
    TotalSummarySection(
        state = state.summaryState,
        selectedType = selectedType,
        onSelectType = { type ->
            onAction(
                ReportsAction.OnTypeSelected(
                    type = type
                )
            )
        }
    )

    BarChartCard(
        state = state.chartState,
        onBarClick = { chartBarItem ->
            onAction(
                ReportsAction.OnBarClicked(
                    barId = chartBarItem.id
                )
            )
        },
        onClearSelection = {
            onAction(
                ReportsAction.OnClearSelectedBar
            )
        }
    )

    CategoryListSection(
        categories = state.categoriesState
    )
}

@Composable
private fun ReportsLoading() {
    TotalSummarySectionPlaceholder()
    BarChartCardPlaceholder()
    CategoryListSectionPlaceholder()
}

@Composable
private fun ReportsError(
    throwable: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ViewPageStatus(
        title = stringResource(R.string.error_something_went_wrong),
        message = throwable.localizedMessage ?: stringResource(R.string.error_failed_to_load_reports),
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
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        actionBtn = {
            ViewPrimaryButton(
                text = stringResource(R.string.btn_retry),
                onClick = onRetry,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        },
        modifier = modifier.padding(vertical = 32.dp)
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ReportsScreenPreview() {
    JetSpendingTheme {
        ReportsContent(
            state = ReportsState(
                periodLabel = "September 2026",
                weeklyBanner = WeeklySummaryBannerState(
                    dateRangeLabel = "Nov 16 – Nov 22",
                    headlineText = UiText.DynamicString("Spent 12% less than last week"),
                    supportingText = UiText.DynamicString("Top category: Food & Drink ($420.00)"),
                    isLowerSpending = true
                ),
                content = UiState.Success(ReportsContentState(
                    summaryState = ReportTotalSummaryState(
                        totalNet = "–900.000",
                        totalIncome = "100.000",
                        totalSpending = "1.000.000",
                        selectedCurrency = AppCurrency.IDR
                    ),
                    chartState = ChartUiState(
                        titleRes = R.string.label_chart_title_monthly_expense,
                        maxAmount = "1.000.000",
                        bars = listOf(
                            ChartBarItemState(
                                id = "1-7",
                                label = "1–7",
                                heightFraction = 0.45f,
                                formattedAmount = "450K",
                                hasValue = true,
                                isHighlighted = false
                            ),
                            ChartBarItemState(
                                id = "8-14",
                                label = "8–14",
                                heightFraction = 1.0f,
                                formattedAmount = "1M",
                                hasValue = true,
                                isHighlighted = true
                            ),
                            ChartBarItemState(
                                id = "15-21",
                                label = "15–21",
                                heightFraction = 0.62f,
                                formattedAmount = "620K",
                                hasValue = true,
                                isHighlighted = false
                            ),
                            ChartBarItemState(
                                id = "22-28",
                                label = "22–28",
                                heightFraction = 0.28f,
                                formattedAmount = "280K",
                                hasValue = true,
                                isHighlighted = false
                            ),
                            ChartBarItemState(
                                id = "29-30",
                                label = "29–30",
                                heightFraction = 0.0f,
                                formattedAmount = "0",
                                hasValue = false,
                                isHighlighted = false
                            )
                        ),
                        selectedCurrency = AppCurrency.IDR,
                        hasAnyData = true
                    ),
                    categoriesState = listOf(
                        CategoryPercentageItemState(
                            categoryUI = CategoryUI(
                                id = "1",
                                sortOrder = 1,
                                name = R.string.category_food,
                                iconRes = R.drawable.ic_food_icon,
                                types = emptyList()
                            ),
                            percentage = 0.7f,
                            percentageText = 0.7f.toLocalePercentageText(),
                            amount = "1.000.000",
                            selectedCurrency = AppCurrency.IDR
                        ),
                        CategoryPercentageItemState(
                            categoryUI = CategoryUI(
                                id = "2",
                                sortOrder = 2,
                                name = R.string.category_food,
                                iconRes = R.drawable.ic_food_icon,
                                types = emptyList()
                            ),
                            percentage = 0.7f,
                            percentageText = 0.7f.toLocalePercentageText(),
                            amount = "1.000.000",
                            selectedCurrency = AppCurrency.IDR
                        ),
                        CategoryPercentageItemState(
                            categoryUI = CategoryUI(
                                id = "3",
                                sortOrder = 3,
                                name = R.string.category_food,
                                iconRes = R.drawable.ic_food_icon,
                                types = emptyList()
                            ),
                            percentage = 0.7f,
                            percentageText = 0.7f.toLocalePercentageText(),
                            amount = "1.000.000",
                            selectedCurrency = AppCurrency.IDR
                        )
                    )
                ))
            ),
            onAction = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ReportsScreenPreview_Loading() {
    JetSpendingTheme {
        ReportsContent(
            state = ReportsState(
                periodLabel = "September 2026",
                content = UiState.Loading
            ),
            onAction = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ReportsScreenPreview_Failure() {
    JetSpendingTheme {
        ReportsContent(
            state = ReportsState(
                periodLabel = "September 2026",
                content = UiState.Failure(IllegalStateException())
            ),
            onAction = {}
        )
    }
}
