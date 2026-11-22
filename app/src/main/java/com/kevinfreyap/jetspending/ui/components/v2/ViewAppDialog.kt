package com.kevinfreyap.jetspending.ui.components.v2

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.model.DialogType
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewAppDialog(
    dialogType: DialogType,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    @DrawableRes iconRes: Int? = null
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        ViewAppDialogContent(
            dialogType = dialogType,
            title = title,
            message = message,
            confirmText = confirmText,
            onConfirm = onConfirm,
            onDismissRequest = onDismissRequest,
            iconRes = iconRes,
            dismissText = dismissText,
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun ViewAppDialogContent(
    dialogType: DialogType,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    @DrawableRes iconRes: Int? = null,
) {
    val accentColor: Color
    val confirmContentColor: Color

    when(dialogType) {
        DialogType.PRIMARY -> {
            accentColor = MaterialTheme.colorScheme.primary
            confirmContentColor = MaterialTheme.colorScheme.onPrimary
        }
        DialogType.WARNING -> {
            accentColor = Theme.custom.statusWarning
            confirmContentColor = Theme.custom.statusOnWarning
        }
        DialogType.DESTRUCTIVE -> {
            accentColor = MaterialTheme.colorScheme.error
            confirmContentColor = MaterialTheme.colorScheme.onError
        }
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = if (iconRes != null) Alignment.CenterHorizontally else Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (iconRes != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.primaryTextColor,
                    textAlign = if (iconRes != null) TextAlign.Center else TextAlign.Start
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor,
                    textAlign = if (iconRes != null) TextAlign.Center else TextAlign.Start
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (dismissText != null) {
                    ViewOutlinedButton(
                        text = dismissText,
                        onClick = onDismiss ?: onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                ViewPrimaryButton(
                    text = confirmText,
                    onClick = {
                        onConfirm()
                    },
                    containerColor = accentColor,
                    contentColor = confirmContentColor,
                    modifier = if (dismissText == null) {
                        Modifier.fillMaxWidth(0.75f)
                    } else {
                        Modifier.weight(1f)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ViewConfirmationDialogPreview() {
    JetSpendingTheme {
        ViewAppDialogContent(
            dialogType = DialogType.WARNING,
            title = "Discard changes?",
            message = "Your unsaved transaction details will be lost.",
            confirmText = "Discard",
            onConfirm = {  },
            onDismissRequest = {  },
            dismissText = "Keep editing",
            onDismiss = {  },
            iconRes = null,
        )
    }
}