package com.kevinfreyap.jetspending.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase
): ViewModel(){
    init {
        viewModelScope.launch {
            categoryUseCase.syncCategoriesFromFirestore()
        }
    }
}