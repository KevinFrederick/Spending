package com.kevinfreyap.jetspending.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.formatter.CurrencyMarkerLabelFormatter
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun rememberCurrencyMarker(
    currency: AppCurrency
): CartesianMarker {
    val label = rememberTextComponent(
        color = Theme.custom.textColor,
        textSize = 12.sp,
        padding = Insets(8f, 4f),
        background = rememberShapeComponent(
            shape = CorneredShape.Pill,
            fill = fill(Theme.custom.nestedCardColor),
            shadow = Shadow(radiusDp = 4f, yDp = 2f)
        )
    )

    val indicatorRear = rememberShapeComponent(shape = CorneredShape.Pill, fill = fill(Color.White))
    val indicatorFront = rememberShapeComponent(shape = CorneredShape.Pill, fill = fill(MaterialTheme.colorScheme.primary))

    val indicator = remember(indicatorRear, indicatorFront) {
        LayeredComponent(
            back = indicatorRear,
            front = indicatorFront,
            padding = Insets(1f, 1f, 1f, 1f),
        )
    }

    val guideline = rememberAxisGuidelineComponent()

    val formatter = remember(currency) {
        CurrencyMarkerLabelFormatter(currency)
    }

    return remember(label, indicator, guideline, formatter) {
        DefaultCartesianMarker(
            label = label,
            valueFormatter = formatter,
            labelPosition = DefaultCartesianMarker.LabelPosition.Top,
            indicator = { indicator },
            indicatorSizeDp = 8f,
            guideline = guideline
        )
    }
}