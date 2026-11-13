package com.kevinfreyap.jetspending.ui.components.v2

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
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
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
                color = Blue500,
                fontWeight = FontWeight.SemiBold
            )) {
                append(textButton)
            }
        },
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .clickable {
                debounceClick()
            }
            .fillMaxWidth()
    )
}

@Preview(
    showBackground = true
)
@Composable
fun ViewAnnotatedTextButtonPreview() {
    JetSpendingTheme {
        ViewAnnotatedTextButton(
            labelText = stringResource(R.string.have_account_already),
            textButton = stringResource(R.string.btn_sign_in),
            onClick = {},
        )
    }
}