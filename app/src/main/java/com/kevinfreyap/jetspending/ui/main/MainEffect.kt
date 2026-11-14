package com.kevinfreyap.jetspending.ui.main

sealed interface MainEffect {
    data object NavigateToOnBoarding: MainEffect
}