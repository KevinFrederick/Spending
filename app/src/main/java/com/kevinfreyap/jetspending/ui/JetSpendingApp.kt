package com.kevinfreyap.jetspending.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.navigation.NavigationItem
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.jetspending.ui.screen.add_transaction.AddTransactionScreen
import com.kevinfreyap.jetspending.ui.screen.dashboard.DashboardScreen

@Composable
fun JetSpendingApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(Screen.Dashboard.route)

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes){
                BottomBar(navController = navController)
            }
        },
        modifier = modifier
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    navigateToAddTransaction = {
                        navController.navigate(Screen.AddTransaction.route)
                    }
                )
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =  navBackStackEntry?.destination?.route

        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.dashboard),
                icon = R.drawable.ic_dashboard_24,
                screen = Screen.Dashboard
            ),
            NavigationItem(
                title = stringResource(R.string.report),
                icon = R.drawable.ic_bar_chart_24,
                screen = Screen.Report
            )
        )

        navigationItems.map { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title
                    )
                },

                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}