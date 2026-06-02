package com.example

import com.example.data.Category

sealed class Screen {
    object Home : Screen() {
        val INSTANCE: Screen = this
    }
    data class CategoryDetails(val category: Category) : Screen()
    object Login : Screen() {
        val INSTANCE: Screen = this
    }
    object AdminDashboard : Screen() {
        val INSTANCE: Screen = this
    }
}
