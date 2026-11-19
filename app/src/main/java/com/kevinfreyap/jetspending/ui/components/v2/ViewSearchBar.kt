package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewSearchBar(
    query: String,
    placeholder: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Theme.custom.primaryTextColor
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 16.dp
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_24),
                        contentDescription = null,
                        tint = Theme.custom.secondaryTextColor,
                        modifier = Modifier
                            .size(20.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        if (query.isBlank()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Theme.custom.secondaryTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }

                    if (query.isNotBlank()) {
                        IconButton(
                            onClick = {onQueryChanged("")},
                            modifier = Modifier
                                .size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close_24),
                                contentDescription = stringResource(R.string.clear_search),
                                tint = Theme.custom.secondaryTextColor,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    trailingIcon?.invoke()
                }
            }
        )
    }
}

@Preview
@Composable
fun ViewSearchBarPreview() {
    JetSpendingTheme {
        ViewSearchBar(
            query = "",
            placeholder = "Search transactions",
            onQueryChanged = {},
            trailingIcon = {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(42.dp)
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(6.dp)
                                    .offset(
                                        x = 2.dp,
                                        y = (-2).dp
                                    )
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tune_24),
                            contentDescription = stringResource(R.string.transaction_filter),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
        )
    }
}