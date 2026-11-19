package com.kevinfreyap.jetspending.ui.components.v2

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.components.ViewErrorTooltip
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

@Composable
fun ViewTextInputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPlaceholder: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE
) {
    val textColor = if (isPlaceholder) {
        Theme.custom.secondaryTextColor.copy(alpha = 0.6f)
    } else {
        Theme.custom.primaryTextColor
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor
                    )
                },
                isError = isError,
                readOnly = readOnly,
                enabled = enabled,
                trailingIcon = if (onClick != null) null else trailingIcon,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                minLines = minLines,
                maxLines = if (singleLine) 1 else maxLines,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceContainer,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,

                    focusedLabelColor = Theme.custom.secondaryTextColor,
                    unfocusedLabelColor = Theme.custom.secondaryTextColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )

            if (onClick != null && enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onClick()
                        }
                )

                if (trailingIcon != null) {
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .matchParentSize()
                            .padding(end = 8.dp)
                    ) {
                        trailingIcon()
                    }
                }
            }
        }


        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewTextInputFieldPreview(){
    JetSpendingTheme {
        ViewTextInputField(
            value = "example@email.com",
            onValueChange = { },
            label = "Email address",
        )
    }
}