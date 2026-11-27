package com.kevinfreyap.jetspending.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

private data class BottomNavItem(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem(
        titleRes = R.string.bottom_nav_home,
        iconRes = R.drawable.ic_home_24,
        route = Screen.Dashboard.route
    ),
    BottomNavItem(
        titleRes = R.string.bottom_nav_transactions,
        iconRes = R.drawable.ic_swap_horizontal_24,
        route = Screen.TransactionList.route
    ),
    BottomNavItem(
        titleRes = R.string.bottom_nav_report,
        iconRes = R.drawable.ic_bar_chart_24,
        route = Screen.Report.ROUTE_WITH_ARGS
    ),
    BottomNavItem(
        titleRes = R.string.bottom_nav_settings,
        iconRes = R.drawable.ic_settings_24,
        route = Screen.Settings.route
    )
)

@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        bottomNavItems.forEach { item ->
            val title = stringResource(item.titleRes)

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = title
                    )
                },

                label = { Text(title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,

                    indicatorColor = Theme.custom.highlightContainer,

                    unselectedIconColor = Theme.custom.secondaryTextColor.copy(alpha = 0.5f),
                    unselectedTextColor = Theme.custom.secondaryTextColor.copy(alpha = 0.5f)
                ),
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    JetSpendingTheme {
        BottomBar(
            currentRoute = "",
            onNavigate = {}
        )
    }
}