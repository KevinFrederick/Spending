package com.kevinfreyap.jetspending.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewBrandName
import com.kevinfreyap.jetspending.ui.components.v2.ViewCurrencyCard
import com.kevinfreyap.jetspending.ui.components.v2.ViewAnnotatedTextButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.navigation.OnboardingNavigation
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun OnboardingScreen(
    onNavigate: (OnboardingNavigation) -> Unit,
    modifier: Modifier = Modifier,
){
    OnboardingContent(
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun OnboardingContent(
    onNavigate: (OnboardingNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewBrandName(
                modifier = Modifier
                    .padding(16.dp)
            )
        },
        bottomBar = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    )
            ) {
                ViewPrimaryButton(
                    text = stringResource(R.string.btn_get_started),
                    onClick = {
                        onNavigate(OnboardingNavigation.NavigateToSignUp)
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_forward_24),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(
                                    start = 8.dp
                                )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                ViewAnnotatedTextButton(
                    labelText = stringResource(R.string.have_account_already),
                    textButton = stringResource(R.string.btn_sign_in),
                    onClick = {
                        onNavigate(OnboardingNavigation.NavigateToSignIn)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ViewCurrencyCard(
                topLabel = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.placeholder_transaction_name),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Theme.custom.secondaryTextColor
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_food_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                },
                centerValue = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.placeholder_transaction_amount),
                            style = MaterialTheme.typography.displaySmall,
                            color = Theme.custom.primaryTextColor,
                            modifier = Modifier.alignByBaseline()
                        )

                        Text(
                            text = stringResource(R.string.placeholder_transaction_currency_code),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                },
                bottomInformation = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_swap_horizontal_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            text = stringResource(R.string.placeholder_transaction_converted_amount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = stringResource(R.string.placeholder_transaction_conversion_rate),
                            style = MaterialTheme.typography.bodySmall,
                            color = Theme.custom.secondaryTextColor,
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )

            Spacer(Modifier.height(28.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_message_onboarding),
                    style = MaterialTheme.typography.displaySmall,
                    color = Theme.custom.primaryTextColor
                )

                Text(
                    text = stringResource(R.string.subtitle_message_onboarding),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Theme.custom.secondaryTextColor
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun OnboardingContentPreview() {
    JetSpendingTheme {
        OnboardingContent(
            onNavigate = {}
        )
    }
}