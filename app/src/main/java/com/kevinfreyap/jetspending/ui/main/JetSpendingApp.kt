package com.kevinfreyap.jetspending.ui.main

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.OfflineBanner
import com.kevinfreyap.jetspending.ui.navigation.DashboardNavigation
import com.kevinfreyap.jetspending.ui.navigation.NavigationItem
import com.kevinfreyap.jetspending.ui.navigation.OnboardingNavigation
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.jetspending.ui.navigation.SignInNavigation
import com.kevinfreyap.jetspending.ui.navigation.SignUpNavigation
import com.kevinfreyap.jetspending.ui.screen.add_transaction.AddTransactionScreen
import com.kevinfreyap.jetspending.ui.screen.dashboard.DashboardScreen
import com.kevinfreyap.jetspending.ui.screen.detail.DetailTransactionScreen
import com.kevinfreyap.jetspending.ui.screen.edit_profile.EditProfileScreen
import com.kevinfreyap.jetspending.ui.screen.list.TransactionListScreen
import com.kevinfreyap.jetspending.ui.screen.notification.NotificationScreen
import com.kevinfreyap.jetspending.ui.screen.onboarding.OnboardingScreen
import com.kevinfreyap.jetspending.ui.screen.privacy_security.PrivacySecurityScreen
import com.kevinfreyap.jetspending.ui.screen.report.ReportScreen
import com.kevinfreyap.jetspending.ui.screen.settings.SettingsScreen
import com.kevinfreyap.jetspending.ui.screen.signin.SignInScreen
import com.kevinfreyap.jetspending.ui.screen.signup.SignUpScreen

@Composable
fun JetSpendingApp(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isOnline by mainViewModel.isOnline.collectAsState()

    val focusManager = LocalFocusManager.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Report.route,
        Screen.Settings.route
    )

    LaunchedEffect(Unit) {
        mainViewModel.emergencyLogoutChannel.collect {
            navController.navigate(Screen.OnBoarding.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
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
                if (currentRoute in bottomBarRoutes) {
                    BottomBar(navController = navController)
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
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.OnBoarding.route) {
                    OnboardingScreen(
                        onNavigate = { destination ->
                            when(destination) {
                                OnboardingNavigation.NavigateToSignIn -> {
                                    navController.navigate(Screen.SignIn.route)
                                }
                                OnboardingNavigation.NavigateToSignUp -> {
                                    navController.navigate(Screen.SignUp.route)
                                }
                            }
                        }
                    )
                }
                composable(Screen.SignUp.route) {
                    SignUpScreen(
                        onNavigate = { destination ->
                            when(destination) {
                                SignUpNavigation.NavigateUp -> {
                                    navController.popBackStack()
                                }
                                SignUpNavigation.NavigateToDashboard -> {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.OnBoarding.route) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                                SignUpNavigation.NavigateToSignIn -> {
                                    navController.navigate(Screen.SignIn.route) {
                                        popUpTo(Screen.OnBoarding.route) {
                                            inclusive = false
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                composable(Screen.SignIn.route) {
                    SignInScreen(
                        onNavigate = {destination ->
                            when(destination) {
                                SignInNavigation.NavigateUp -> navController.popBackStack()
                                SignInNavigation.NavigateToSignUp -> {
                                    navController.navigate(Screen.SignUp.route) {
                                        popUpTo (Screen.OnBoarding.route) {
                                            inclusive = false
                                        }
                                    }
                                }
                                SignInNavigation.NavigateToDashboard -> {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.OnBoarding.route) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    )
                }
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onNavigate = { destination ->
                            when(destination) {
                                is DashboardNavigation.NavigateToTransactionDetail -> {
                                    navController.navigate(
                                        Screen.TransactionDetail.createRoute(destination.transactionId)
                                    )
                                }
                                DashboardNavigation.NavigateToAddTransaction -> {
                                    navController.navigate(Screen.AddTransaction.route)
                                }
                                DashboardNavigation.NavigateToTransactionList -> {
                                    navController.navigate(Screen.TransactionList.route)
                                }
                            }
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navigateToOnBoarding = {
                            navController.navigate(Screen.OnBoarding.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        },
                        navigateToEditProfile = {
                            navController.navigate(Screen.EditProfile.route)
                        },
                        navigateToNotification = {
                            navController.navigate(Screen.Notification.route)
                        },
                        navigateToPrivacySecurity = {
                            navController.navigate(Screen.PrivacySecurity.route)
                        }
                    )
                }
                composable(Screen.Report.route) {
                    ReportScreen()
                }
                composable(
                    route = Screen.AddTransaction.ROUTE_WITH_ARGS,
                    arguments = listOf(
                        navArgument(
                            "transactionId"
                        ) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { _ ->
                    AddTransactionScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.TransactionList.route) {
                    TransactionListScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        navigateToDetail = { transactionId ->
                            navController.navigate(
                                Screen.TransactionDetail.createRoute(
                                    transactionId
                                )
                            )
                        }
                    )
                }
                composable(
                    route = Screen.TransactionDetail.route,
                    arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val transactionId = backStackEntry.arguments?.getString("transactionId")

                    DetailTransactionScreen(
                        transactionId = transactionId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        navigateToUpdate = { transactionId ->
                            val id = transactionId ?: return@DetailTransactionScreen
                            navController.navigate(Screen.AddTransaction.createRoute(transactionId = id))
                        }
                    )
                }
                composable(Screen.EditProfile.route) {
                    EditProfileScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.Notification.route) {
                    NotificationScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.PrivacySecurity.route) {
                    PrivacySecurityScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        navigateToOnBoarding = {
                            navController.navigate(Screen.OnBoarding.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        },
                    )
                }
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
            ),
            NavigationItem(
                title = stringResource(R.string.settings),
                icon = R.drawable.ic_settings_24,
                screen = Screen.Settings
            )
        )

        navigationItems.forEach { item ->
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