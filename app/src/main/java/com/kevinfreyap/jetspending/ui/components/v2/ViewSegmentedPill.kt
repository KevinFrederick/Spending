package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.helper.filter.toUiNameRes
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun <T> ViewSegmentedPill(
    options: List<T>,
    selectedOption: T,
    optionLabel: @Composable (T) -> String,
    onSelectOption: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val isStretched = constraints.hasFixedWidth

        Surface(
            shape = RoundedCornerShape(100),
            color = Color.Transparent,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
            modifier = if (isStretched) Modifier.fillMaxWidth() else Modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = option == selectedOption

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .then(if (isStretched) Modifier.weight(1f) else Modifier)
                            .fillMaxHeight()
                            .selectable(
                                selected = isSelected,
                                role = Role.Tab,
                                onClick = { onSelectOption(option) }
                            )
                            .background(
                                if (isSelected) Theme.custom.highlightContainer else Color.Transparent
                            )
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                    ) {
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Theme.custom.secondaryTextColor
                            },
                            fontWeight = if (isSelected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    }

                    if (index < options.lastIndex) {
                        VerticalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ViewSegmentedPillPreview() {
    JetSpendingTheme {
        ViewSegmentedPill(
            options = TransactionType.entries,
            selectedOption = TransactionType.SPENDING,
            optionLabel = { stringResource(it.toUiNameRes()) },
            onSelectOption = {  },
        )
    }
}