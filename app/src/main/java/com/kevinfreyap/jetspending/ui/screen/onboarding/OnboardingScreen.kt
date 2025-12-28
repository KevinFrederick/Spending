package com.kevinfreyap.jetspending.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewGoogleBtn
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.findActivity
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    onGetStartedClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    navigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val showSuccessDialog by viewModel.showDialog.collectAsState()

    OnboardingContent(
        showSuccessDialog = showSuccessDialog,
        uiState = uiState,
        onGetStartedClicked = onGetStartedClicked,
        onGoogleClicked = {
            val activity = context.findActivity()
            activity?.let {
                viewModel.onAuthWithGoogle(activity)
            }
        },
        onSignInClicked = onSignInClicked,
        onDismissDialog = {
            viewModel.onDismissDialog()
            navigateToDashboard()
        },
        modifier = modifier
    )
}

@Composable
fun OnboardingContent(
    showSuccessDialog: Boolean,
    uiState: UiState<Unit>,
    onGetStartedClicked: () -> Unit,
    onGoogleClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = uiState is UiState.Loading

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.background
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                Theme.custom.onBoardCardColor
                            )
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = "App Icon"
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displaySmall
                    )

                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Buttons Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Button(
                    onClick = onGetStartedClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.get_started),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )

                ViewGoogleBtn(
                    onClick = onGoogleClicked
                )

                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.have_account_already))
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = Blue500,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(stringResource(R.string.sign_in))
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.textColor,
                    modifier = Modifier
                        .clickable(
                            onClick = onSignInClicked
                        )
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    }

    if (showSuccessDialog) {
        LaunchedEffect(Unit) {
            delay(2000)
            onDismissDialog()
        }

        ViewCustomDialog(
            onDismissRequest = onDismissDialog,
            icon = R.drawable.ic_check_circle_outline_24,
            iconColor = Green500,
            title = stringResource(R.string.success),
            message = stringResource(R.string.success_message_welcome)
        )
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
            onSignInClicked = {},
            onGetStartedClicked = {},
            showSuccessDialog = false,
            uiState = UiState.Success(Unit),
            onGoogleClicked = {},
            onDismissDialog = {},
        )
    }
}