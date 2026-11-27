package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun <T> ViewDropdownField(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    label: String,
    optionLabel: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    itemContent: (@Composable (option: T, isSelected: Boolean) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevronRotation"
    )

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = {
            if (enabled) expanded = it
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        ViewTextInputField(
            value = optionLabel(selectedOption),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            label = label,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_keyboard_arrow_down_24),
                    contentDescription = null,
                    tint = Theme.custom.secondaryTextColor,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(rotation)
                )
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(20.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOption == option

                DropdownMenuItem(
                    text = {
                        if (itemContent != null) {
                            itemContent(option, isSelected)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = optionLabel(option),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Theme.custom.primaryTextColor
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                if (isSelected) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ViewDropdownFieldPreview() {
    JetSpendingTheme {
        var selected by remember { mutableStateOf(AppCurrency.IDR) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(16.dp)
        ) {
            ViewDropdownField(
                options = AppCurrency.entries,
                selectedOption = selected,
                onOptionSelected = { selected = it },
                label = "Currency",
                optionLabel = { it.displayCodeName },
            )
        }
    }
}
