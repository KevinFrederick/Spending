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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewGoogleBtn
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun OnboardingScreen(
    onGetStartedClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier
){
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
                .padding(horizontal = 64.dp)
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

            ViewGoogleBtn()

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.have_account_already))
                    append(" ")
                    withStyle(style = SpanStyle(
                        color = Blue500,
                        fontWeight = FontWeight.SemiBold
                    )) {
                        append(stringResource(R.string.sign_in))
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable {
                        onSignInClicked()
                    }
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun OnboardingContentView() {
    JetSpendingTheme {
        OnboardingScreen(
            onSignInClicked = {},
            onGetStartedClicked = {},
        )
    }
}