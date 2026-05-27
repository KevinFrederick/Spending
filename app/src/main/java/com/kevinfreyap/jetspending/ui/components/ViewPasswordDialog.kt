package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewPasswordDialog(
    title: String,
    subtitle: String?,
    onDismissRequest: () -> Unit,
    password: @Composable () -> Unit,
    positiveBtn: @Composable () -> Unit,
    negativeBtn: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    newPassword: (@Composable () -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        ViewPasswordDialogContent(
            title = title,
            subtitle = subtitle,
            password = password,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn,
            modifier = modifier,
            isLoading = isLoading,
            newPassword = newPassword
        )
    }
}

@Composable
fun ViewPasswordDialogContent(
    title: String,
    subtitle: String?,
    password: @Composable () -> Unit,
    positiveBtn: @Composable () -> Unit,
    negativeBtn: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    newPassword: (@Composable () -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
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
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineSmall,
                color = Theme.custom.textColor,
                textAlign = TextAlign.Center,
            )

            subtitle?.let { sub ->
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.textColor,
                    textAlign = TextAlign.Start,
                )
            }

            if (isLoading) {
                LinearProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            password()

            newPassword?.invoke()

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                negativeBtn()

                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                )

                positiveBtn()
            }

        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewPasswordDialogPreview() {
    JetSpendingTheme {
        ViewPasswordDialogContent(
            title = "Change Password",
            subtitle = "Enter your current password and new password.",
            password = {
                ViewTextField(
                    value = "",
                    onValueChange = {},
                    label = "",
                    placeholder = "Current Password"
                )
            },
            newPassword = {
                ViewTextField(
                    value = "",
                    onValueChange = {},
                    label = "",
                    placeholder = "New Password"
                )
            },
            positiveBtn = {
                Button(
                    onClick = {}
                ) {
                    Text(
                        text = "Confirm"
                    )
                }
            },
            negativeBtn = {
                Button(
                    onClick = {}
                ) {
                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}