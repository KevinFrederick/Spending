package com.kevinfreyap.jetspending.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kevinfreyap.jetspending.ui.navigation.DashboardNavigation
import com.kevinfreyap.jetspending.ui.navigation.EditProfileNavigation
import com.kevinfreyap.jetspending.ui.navigation.OnboardingNavigation
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.jetspending.ui.navigation.SettingsNavigation
import com.kevinfreyap.jetspending.ui.navigation.SignInNavigation
import com.kevinfreyap.jetspending.ui.navigation.SignUpNavigation
import com.kevinfreyap.jetspending.ui.navigation.TransactionDetailNavigation
import com.kevinfreyap.jetspending.ui.navigation.TransactionFormNavigation
import com.kevinfreyap.jetspending.ui.navigation.TransactionListNavigation
import com.kevinfreyap.jetspending.ui.screen.dashboard.DashboardScreen
import com.kevinfreyap.jetspending.ui.screen.detail.TransactionDetailScreen
import com.kevinfreyap.jetspending.ui.screen.edit_profile.EditProfileScreen
import com.kevinfreyap.jetspending.ui.screen.list.TransactionListScreen
import com.kevinfreyap.jetspending.ui.screen.notification.NotificationScreen
import com.kevinfreyap.jetspending.ui.screen.onboarding.OnboardingScreen
import com.kevinfreyap.jetspending.ui.screen.privacy_security.PrivacySecurityScreen
import com.kevinfreyap.jetspending.ui.screen.report.ReportsScreen
import com.kevinfreyap.jetspending.ui.screen.settings.SettingsScreen
import com.kevinfreyap.jetspending.ui.screen.signin.SignInScreen
import com.kevinfreyap.jetspending.ui.screen.signup.SignUpScreen
import com.kevinfreyap.jetspending.ui.screen.transaction_form.TransactionFormScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
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
                            navController.navigate(Screen.TransactionForm.route)
                        }
                        DashboardNavigation.NavigateToTransactionList -> {
                            navController.navigate(Screen.TransactionList.route) {
                                popUpTo (navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigate = { destination ->
                    when(destination) {
                        SettingsNavigation.NavigateToEditProfile -> {
                            navController.navigate(Screen.EditProfile.route)
                        }
                        SettingsNavigation.NavigateToNotification -> {
                            navController.navigate(Screen.Notification.route)
                        }
                        SettingsNavigation.NavigateToOnBoard -> {
                            navController.navigate(Screen.OnBoarding.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                        SettingsNavigation.NavigateToSecurity -> {
                            navController.navigate(Screen.PrivacySecurity.route)
                        }
                    }
                }
            )
        }

        composable(Screen.Report.route) {
            ReportsScreen()
        }

        composable(
            route = Screen.TransactionForm.ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument(
                    "transactionId"
                ) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            TransactionFormScreen(
                onNavigate = { destination ->
                    when(destination) {
                        TransactionFormNavigation.NavigateUp -> navController.popBackStack()
                    }
                }
            )
        }
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onNavigate = {destination ->
                    when(destination) {
                        is TransactionListNavigation.NavigateToTransactionDetail -> {
                            navController.navigate(
                                Screen.TransactionDetail.createRoute(destination.transactionId)
                            )
                        }
                        is TransactionListNavigation.NavigateToAddTransaction -> {
                            navController.navigate(
                                Screen.TransactionForm.route
                            )
                        }
                    }
                },
            )
        }
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) {
            TransactionDetailScreen(
                onNavigate = { destination ->
                    when(destination) {
                        is TransactionDetailNavigation.NavigateUp -> navController.popBackStack()
                        is TransactionDetailNavigation.NavigateToEditTransaction -> {
                            navController.navigate(
                                Screen.TransactionForm.createRoute(
                                    transactionId = destination.transactionId
                                )
                            )

                        }
                    }

                }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigate = { destination ->
                    when(destination) {
                        EditProfileNavigation.NavigateUp -> {
                            navController.popBackStack()
                        }
                    }
                },
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