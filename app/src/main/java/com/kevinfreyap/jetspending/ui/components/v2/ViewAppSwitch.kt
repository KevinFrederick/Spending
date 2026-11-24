package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewAppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        thumbContent = {
            Box(modifier = Modifier.size(16.dp))
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.surface,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor = Theme.custom.secondaryTextColor,
            uncheckedTrackColor = Theme.custom.inactiveContainer,
            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Preview(
    showBackground = true
)
@Composable
fun ViewAppSwitchPreview() {
    JetSpendingTheme {
        var checked by remember { mutableStateOf(false) }

        ViewAppSwitch(
            checked = checked,
            onCheckedChange = { newValue -> checked = newValue },
        )
    }
}