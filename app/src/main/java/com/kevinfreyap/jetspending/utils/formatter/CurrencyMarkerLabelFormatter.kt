package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.model.AppCurrency
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import java.math.BigDecimal
import kotlin.math.roundToInt

class CurrencyMarkerLabelFormatter(
    private val currency: AppCurrency
): DefaultCartesianMarker.ValueFormatter {
    override fun format(
        context: CartesianDrawingContext,
        targets: List<CartesianMarker.Target>
    ): CharSequence {
        val xIndex = targets.firstOrNull()?.x?.roundToInt() ?: return ""

        val columnModel = context.model.models
            .filterIsInstance<ColumnCartesianLayerModel>()
            .firstOrNull() ?: return ""

        return columnModel.series.mapIndexedNotNull { index, entries ->
            val entry = entries.find { it.x.roundToInt() == xIndex }
            val amount = entry?.y?.toBigDecimal() ?: BigDecimal.ZERO

            val label = if (index == 0) "Inc" else "Exp"

            if (amount != BigDecimal.ZERO) {
                "$label: ${CurrencyUiFormatter.formatWithCode(amount, currency)}"
            } else {
                null
            }
        }.joinToString("  |  ")
    }
}