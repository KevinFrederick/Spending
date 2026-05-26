package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewCustomTimeInput(
    initialHour: String,
    initialMinute: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    var isHourFocused by remember { mutableStateOf(false) }
    var isMinuteFocused by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        BasicTextField(
            value = hour,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }.take(2)
                if (filtered.isEmpty() || (filtered.toIntOrNull() ?: 0) <= 23) {
                    hour = filtered
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            textStyle = MaterialTheme.typography.displayLarge.copy(
                color = when {
                    isHourFocused -> Theme.custom.textColor
                    isMinuteFocused -> Theme.custom.hintColor
                    else -> Theme.custom.textColor
                },
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .size(
                    96.dp,
                    80.dp
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.custom.nestedCardColor)
                .onFocusChanged { focusState ->
                    isHourFocused = focusState.hasFocus

                    if (!focusState.hasFocus && hour.isEmpty()) {
                        hour = initialHour
                    }

                    if (!focusState.hasFocus && hour.isNotEmpty()) {
                        hour = hour.padStart(2, '0')
                        onTimeChange("$hour:$minute")
                    }
                }
        ) { innerTextField ->
            Box(
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }

        Text(
            text = ":",
            style = MaterialTheme.typography.displayLarge,
            color = Theme.custom.textColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        BasicTextField(
            value = minute,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }.take(2)
                if (filtered.isEmpty() || (filtered.toIntOrNull() ?: 0) <= 59) {
                    minute = filtered
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            textStyle = MaterialTheme.typography.displayLarge.copy(
                color = when {
                    isHourFocused -> Theme.custom.hintColor
                    isMinuteFocused -> Theme.custom.textColor
                    else -> Theme.custom.textColor
                },
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .size(
                    96.dp,
                    80.dp
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.custom.nestedCardColor)
                .onFocusChanged { focusState ->
                    isMinuteFocused = focusState.hasFocus

                    if (!focusState.hasFocus && minute.isEmpty()) {
                        minute = initialMinute
                    }

                    if (!focusState.hasFocus && minute.isNotEmpty()) {
                        minute = minute.padStart(2, '0')
                        onTimeChange("$hour:$minute")
                    }
                }
        ) { innerTextField ->
            Box(
                contentAlignment = Alignment.Center
            ) { innerTextField() }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewCustomTimeInputPreview() {
    JetSpendingTheme {
        ViewCustomTimeInput(
            initialHour = "21",
            initialMinute = "00",
            onTimeChange = {}
        )
    }
}