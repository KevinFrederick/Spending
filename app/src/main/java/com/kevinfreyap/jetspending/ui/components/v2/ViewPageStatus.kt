package com.kevinfreyap.jetspending.ui.components.v2

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewPageStatus(
    title: String,
    message: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actionBtn: (@Composable () -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        icon()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Theme.custom.primaryTextColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.custom.secondaryTextColor,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        actionBtn?.let {
            Spacer(modifier = Modifier.height(24.dp))

            actionBtn()
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ViewPageStatusPreview() {
    JetSpendingTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ViewPageStatus(
                title = "Error loading transactions",
                message = "We couldn't load your transactions right now. Check your connection and try again.",
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_priority_high_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                },
                actionBtn = {
                    ViewPrimaryButton(
                        text = "Retry",
                        onClick = {},
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_refresh_24),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                }
            )
        }
    }
}