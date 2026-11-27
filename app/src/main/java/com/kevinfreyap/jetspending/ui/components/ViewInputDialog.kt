package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewInputDialog(
    title: String,
    subtitle: String?,
    positiveText: String,
    onDismissRequest: () -> Unit,
    onPositiveClicked: () -> Unit,
    textField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes negativeTextRes: Int = R.string.btn_cancel,
    positiveBtnContainerColor: Color = MaterialTheme.colorScheme.primary,
    positiveBtnContentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        ViewInputDialogContent(
            title = title,
            subtitle = subtitle,
            positiveText = positiveText,
            onDismissRequest = onDismissRequest,
            onPositiveClicked = onPositiveClicked,
            textField = textField,
            modifier = modifier,
            negativeTextRes = negativeTextRes,
            positiveBtnContainerColor = positiveBtnContainerColor,
            positiveBtnContentColor = positiveBtnContentColor
        )
    }
}

@Composable
private fun ViewInputDialogContent(
    title: String,
    subtitle: String?,
    positiveText: String,
    @StringRes negativeTextRes: Int,
    onDismissRequest: () -> Unit,
    onPositiveClicked: () -> Unit,
    textField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    positiveBtnContainerColor: Color = MaterialTheme.colorScheme.primary,
    positiveBtnContentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Theme.custom.primaryTextColor,
            )

            subtitle?.let { sub ->
                Text(
                    text = sub,
                    style = MaterialTheme.typography.labelMedium,
                    color = Theme.custom.secondaryTextColor,
                )
            }

            Spacer(
                modifier = Modifier
                    .height(12.dp)
            )

            textField()

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = stringResource(negativeTextRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = Theme.custom.secondaryTextColor
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )

                ViewPrimaryButton(
                    text = positiveText,
                    onClick = onPositiveClicked,
                    containerColor = positiveBtnContainerColor,
                    contentColor = positiveBtnContentColor
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
private fun ViewInputDialogPreview() {
    JetSpendingTheme {
        ViewInputDialogContent(
            title = "Reset Password",
            subtitle = "Enter your email to receive a reset link",
            positiveText = "Confirm",
            onDismissRequest = {},
            textField = {
                ViewTextInputField(
                    value = "",
                    onValueChange = {
                    },
                    label = stringResource(R.string.label_email),
                )
            },
            onPositiveClicked = {},
            negativeTextRes = R.string.btn_cancel,
        )
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
private fun ViewInputDialogPreview_2Fields() {
    JetSpendingTheme {
        ViewInputDialogContent(
            title = "Change password",
            subtitle = "Enter your email to receive a reset link",
            positiveText = "Confirm",
            onDismissRequest = {},
            textField = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ViewTextInputField(
                        value = "",
                        onValueChange = {

                        },
                        label = stringResource(R.string.label_current_password),
                    )

                    ViewTextInputField(
                        value = "",
                        onValueChange = {
                        },
                        label = stringResource(R.string.label_new_password),
                    )
                }
            },
            onPositiveClicked = {},
            negativeTextRes = R.string.btn_cancel,
        )
    }
}