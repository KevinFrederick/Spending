package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewGoogleBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    btnText: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Theme.custom.primaryTextColor
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
            tint = null
        )

        Spacer(
            modifier = Modifier
                .width(8.dp)
        )

        Text(
            text = btnText ?: stringResource(R.string.continue_with_google),
            style = MaterialTheme.typography.labelLarge,
            color = Theme.custom.primaryTextColor,
        )
    }
}

@Preview (
    showBackground = true
)
@Composable
fun ViewGoogleBtnPreview() {
    JetSpendingTheme {
        ViewGoogleBtn(
            onClick = {}
        )
    }
}