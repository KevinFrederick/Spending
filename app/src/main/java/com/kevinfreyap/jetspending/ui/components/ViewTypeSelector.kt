package com.kevinfreyap.jetspending.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.theme.Grey600
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewTypeSelector(
    selectedOption: TransactionType?,
    onSelectOption: (TransactionType) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = stringResource(R.string.type)
) {
    val focusManager = LocalFocusManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .selectableGroup()
    ) {
        if (label != null) {
            Text(
                text = label,
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
        }


        TransactionType.entries.forEach { type ->
            val text = when (type) {
                TransactionType.INCOME -> stringResource(R.string.income)
                TransactionType.SPENDING -> stringResource(R.string.spending)
            }

            PillRadioButton(
                text = text,
                selected = (type == selectedOption),
                onClick = {
                    focusManager.clearFocus()
                    onSelectOption(type)
                }
            )

            if (type != TransactionType.entries.last()) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun PillRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Theme.custom.nestedCardColor,
        label = "bg"
    )

    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimary else Grey600,
        label = "content"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(
                horizontal = 24.dp,
                vertical = 12.dp
            )
    ){
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewTypeSelectorPreview(){
    JetSpendingTheme {
        ViewTypeSelector(
            selectedOption = TransactionType.SPENDING,
            onSelectOption = {}
        )
    }
}