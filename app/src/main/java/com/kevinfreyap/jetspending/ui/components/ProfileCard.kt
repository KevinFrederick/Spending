package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ProfileCard(
    imageUrl: String?,
    name: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ViewUserProfile(
                imageUrl = imageUrl,
                name = name,
                size = 64.dp
            )

            Spacer(
                modifier = Modifier
                    .width(12.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Theme.custom.textColor
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.textColor
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ProfileCardPreview() {
    JetSpendingTheme {
        ProfileCard(
            imageUrl = null,
            name = "John Doe",
            email = "JohnDoe@email.com"
        )
    }
}