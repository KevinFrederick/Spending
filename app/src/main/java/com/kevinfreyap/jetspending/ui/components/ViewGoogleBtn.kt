package com.kevinfreyap.jetspending.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewGoogleBtn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            Theme.custom.textColor
        ),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Theme.custom.textColor
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "Google",
            tint = null
        )

        Spacer(
            modifier = Modifier
                .width(8.dp)
        )

        Text(
            text = stringResource(R.string.continue_with_google),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}