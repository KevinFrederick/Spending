package com.kevinfreyap.jetspending.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.OfflineBanner
import com.kevinfreyap.jetspending.ui.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun JetSpendingApp(
    startDestination: String,
    isOnline: Boolean,
    effects: Flow<MainEffect>,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val focusManager = LocalFocusManager.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = remember(currentRoute)  {
        currentRoute in setOf(
            Screen.Dashboard.route,
            Screen.Report.route,
            Screen.Settings.route
        )
    }

    LaunchedEffect(effects, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                effects.collect { effect ->
                    when(effect) {
                        MainEffect.NavigateToOnBoarding -> {
                            navController.navigate(Screen.OnBoarding.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !isOnline,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OfflineBanner()
        }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    restoreState = true
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                .exclude(WindowInsets.statusBars),
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

private data class BottomNavItem(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem(
        titleRes = R.string.dashboard,
        iconRes = R.drawable.ic_dashboard_24,
        route = Screen.Dashboard.route
    ),
    BottomNavItem(
        titleRes = R.string.report,
        iconRes = R.drawable.ic_bar_chart_24,
        route = Screen.Report.route
    ),
    BottomNavItem(
        titleRes = R.string.settings,
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
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}