package com.nofy.feature.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val isServiceActive: Boolean = false,
)
