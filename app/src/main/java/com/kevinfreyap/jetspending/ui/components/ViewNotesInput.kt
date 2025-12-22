package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewNotesInput (
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        readOnly = readOnly,
        isError = isError,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.notes)
            )
        },
        supportingText = { if (!readOnly) {
            Text(
                text = "${value.length} / 1000",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
            )}
        },
        minLines = 5,
        maxLines = 7,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Theme.custom.cardColor,
            focusedContainerColor = Theme.custom.cardColor,
            unfocusedTextColor = Theme.custom.textColor,
            focusedTextColor = Theme.custom.textColor,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
            )
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun ViewNotesInputPreview() {
    JetSpendingTheme {
        ViewNotesInput(
            value = "",
            onValueChange = {},
            isError = false
        )
    }
}