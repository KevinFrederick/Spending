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
fun ViewInputDialog(
    title: String,
    subtitle: String?,
    onDismissRequest: () -> Unit,
    textField: @Composable () -> Unit,
    positiveBtn: @Composable () -> Unit,
    negativeBtn: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest
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
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(8.dp)
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
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewInputDialogPreview() {
    JetSpendingTheme {
        ViewInputDialog(
            title = "Reset Password",
            subtitle = "Enter your email to receive a reset link",
            onDismissRequest = {},
            textField = {
                ViewTextField(
                    value = "",
                    onValueChange = {},
                    label = "",
                    placeholder = "Email"
                )
            },
            positiveBtn = {
                Button(
                    onClick = {}
                ) {
                    Text(
                        text = "Send Link"
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