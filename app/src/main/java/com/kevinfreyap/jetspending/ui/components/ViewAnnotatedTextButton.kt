package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.debouncedClick

@Composable
fun ViewAnnotatedTextButton(
    labelText: String,
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val debounceClick = debouncedClick(onClick = onClick)
    
    Text(
        text = buildAnnotatedString {
            append(labelText)
            append(" ")
            withStyle(style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )) {
                append(textButton)
            }
        },
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = Theme.custom.primaryTextColor,
        modifier = modifier
            .clickable {
                debounceClick()
            }
            .fillMaxWidth()
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ViewAnnotatedTextButtonPreview() {
    JetSpendingTheme {
        ViewAnnotatedTextButton(
            labelText = stringResource(R.string.annotated_label_have_account_already),
            textButton = stringResource(R.string.btn_sign_in),
            onClick = {},
        )
    }
}