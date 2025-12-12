package com.kevinfreyap.jetspending.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.jetspending.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val categoryUseCase: CategoryUseCase
): ViewModel(){
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            categoryUseCase.syncCategoriesFromFirestore()

            val isLoggedIn = authenticationUseCase.isUserLoggedIn()
            _startDestination.value = if (isLoggedIn) Screen.Dashboard.route else Screen.OnBoarding.route
        }
    }
}