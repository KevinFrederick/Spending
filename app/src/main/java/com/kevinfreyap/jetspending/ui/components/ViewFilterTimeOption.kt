package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun ViewFilterTimeOption(
    text: String,
    isSelected: Boolean,
    onOptionClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(50)
            )
            .clickable(
                role = Role.RadioButton,
                onClick = onOptionClicked
            )
            .padding(
                vertical = 8.dp,
                horizontal = 8.dp
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )

        RadioButton(
            selected = isSelected,
            onClick = null
        )
    }
}