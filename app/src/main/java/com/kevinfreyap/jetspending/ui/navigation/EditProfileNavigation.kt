package com.kevinfreyap.jetspending.ui.navigation

sealed interface EditProfileNavigation {
    data object NavigateUp: EditProfileNavigation
}