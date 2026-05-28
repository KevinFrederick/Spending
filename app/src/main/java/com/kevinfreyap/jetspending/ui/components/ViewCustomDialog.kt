package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewCustomDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    title: String? = null,
    message: String? = null,
    icon: Int? = null,
    iconColor: Color? = null,
    textField: @Composable (() -> Unit)? = null,
    positiveBtn: @Composable (() -> Unit)? = null,
    negativeBtn: @Composable (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        ViewCustomDialogContent(
            modifier = modifier,
            isLoading = isLoading,
            title = title,
            message = message,
            icon = icon,
            iconColor = iconColor,
            textField = textField,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn
        )
    }
}

@Composable
fun ViewCustomDialogContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    title: String? = null,
    message: String? = null,
    icon: Int? = null,
    iconColor: Color? = null,
    textField: @Composable (() -> Unit)? = null,
    positiveBtn: @Composable (() -> Unit)? = null,
    negativeBtn: @Composable (() -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(if (icon == null) 16.dp else 24.dp)
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "Dialog Icon",
                    tint = iconColor ?: Theme.custom.iconColor,
                    modifier = Modifier
                        .size(76.dp)
                        .align(
                            alignment = Alignment.CenterHorizontally
                        )
                )
            }

            if (title != null) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Theme.custom.textColor,
                    textAlign = if (icon == null) TextAlign.Start else TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (icon == null) 0.dp else 8.dp
                        )
                )
            }

            if (message != null) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Theme.custom.textColor,
                    textAlign = if (icon == null) TextAlign.Start else TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (icon == null) 0.dp else 4.dp)
                )
            }

            if (isLoading) {
                LinearProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            textField?.invoke()

            if (positiveBtn != null || negativeBtn != null){
                Row(
                    horizontalArrangement = if (icon == null) Arrangement.spacedBy(8.dp) else Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (icon == null) 8.dp else 16.dp
                        )
                ) {
                    if (icon == null) {
                        if (positiveBtn != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                positiveBtn.invoke()
                            }
                        }

                        if (negativeBtn != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                negativeBtn.invoke()
                            }
                        }
                    } else {
                        negativeBtn?.invoke()
                        positiveBtn?.invoke()
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewCustomDialogPreview() {
    JetSpendingTheme {
        ViewCustomDialogContent(
            icon = R.drawable.ic_check_circle_outline_24,
            iconColor = Green500,
            title = "Success",
            message = "Transaction Saved Successfully",
//            textField = {
//                ViewTextField(
//                    value = "",
//                    onValueChange = {},
//                    label = "",
//                    placeholder = "Current Password"
//                )
//            },
            negativeBtn = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Grey500
                    ),
                    onClick = {},
                ) {
                    Text(
                        text = "Cancel"
                    )
                }
            },
            positiveBtn = {
                Button(
                    onClick = {}
                ) {
                    Text(
                        text = "Confirm"
                    )
                }
            }
        )
    }
}