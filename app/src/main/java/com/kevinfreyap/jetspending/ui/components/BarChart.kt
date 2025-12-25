package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.rememberCurrencyMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.layer.grouped
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.common.component.LineComponent
import kotlinx.coroutines.runBlocking

@Composable
fun BarChart (
    modelProducer: CartesianChartModelProducer,
    bottomLabel: CartesianValueFormatter,
    startLabel: CartesianValueFormatter,
    marker: CartesianMarker,
    modifier: Modifier = Modifier
) {
    Card (
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.transaction_chart),
                style = MaterialTheme.typography.titleMedium,
                color = Theme.custom.textColor,
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = 8.dp
                    )
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            LineComponent(
                                fill = fill(Green500),
                                thicknessDp = 12f
                            ),
                            LineComponent(
                                fill = fill(Orange700),
                                thicknessDp = 12f
                            )
                        ),
                        mergeMode = {
                            ColumnCartesianLayer.MergeMode.grouped(
                                columnSpacing = 2.dp
                            )
                        }
                    ),
                    startAxis = null,
                    endAxis = VerticalAxis.rememberEnd(
                        valueFormatter = startLabel
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        guideline = null,
                        valueFormatter = bottomLabel
                    ),
                    marker = marker
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun BarChartPreview() {
    val modelProducer = remember { CartesianChartModelProducer() }

    val dataMap = remember {
        linkedMapOf(
            "Mon" to 50000,
            "Tue" to 12000,
            "Wed" to 8000,
            "Thu" to 16000,
            "Fri" to 12000,
            "Sat" to 8000,
            "Sun" to 5000
        )
    }

    val testMap = remember {
        linkedMapOf(
            "Mon" to 5000,
            "Tue" to 22000,
            "Wed" to 8000,
            "Thu" to 15000,
            "Fri" to 10000,
            "Sat" to 8000,
            "Sun" to 5000
        )
    }

    val bottomValueFormatter = CartesianValueFormatter { x, value, _ ->
        dataMap.keys.elementAtOrElse(value.toInt()) { "" }
    }

    val startValueFormatter = CartesianValueFormatter { x, value, _ ->
        CurrencyUiFormatter.formatWithCode(value.toBigDecimal(), AppCurrency.IDR)
    }

    // 2. Load dummy data immediately
    runBlocking {
        modelProducer.runTransaction {
            // Add a line series with hardcoded values for the preview
            columnSeries {
                series(dataMap.values)
                series(testMap.values)
            }
        }
    }
    JetSpendingTheme {
        BarChart(
            modelProducer,
            bottomValueFormatter,
            startValueFormatter,
            marker = rememberCurrencyMarker(AppCurrency.IDR)
        )
    }
}