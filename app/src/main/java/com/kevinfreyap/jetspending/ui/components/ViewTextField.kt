package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = if (placeholder.isNullOrBlank()) {
            @Composable { Text(label) }
        } else {
            null
        },
        placeholder = if (!placeholder.isNullOrBlank()) {
            @Composable {
                Text(
                    placeholder,
                    color = Theme.custom.hintColor
                )
            }
        } else {
            null
        },
        enabled = isEnabled,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        singleLine = true,
        shape = shape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Theme.custom.cardColor,
            focusedContainerColor = Theme.custom.cardColor,
            errorContainerColor = Theme.custom.cardColor,
            unfocusedLabelColor = Theme.custom.hintColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledContainerColor = Theme.custom.cardColor,
            disabledTextColor = Theme.custom.hintColor,
            disabledLabelColor = Theme.custom.hintColor
        ),
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        trailingIcon = {
            Row {
                trailingIcon?.invoke()

                if (isError) {
                    ViewErrorTooltip(
                        errorMessage = errorMessage
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = if (isError) 2.dp else 0.dp,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
                shape = shape
            )
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ViewTextFieldPreview(){
    var testTextInput by remember { mutableStateOf("") }
    JetSpendingTheme {
        ViewTextField(
            value = testTextInput,
            onValueChange = {
                testTextInput = it
            },
            label = "Text Field Label",
            placeholder = "Text",
            isError = false,
            isEnabled = false,
            errorMessage = "Required"
        )
    }
}