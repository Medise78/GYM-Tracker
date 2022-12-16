package com.medise.bashga.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routes(val route: String, val icon: ImageVector? = null, val title: String = "") {
    object HomeScreen : Routes("home_screen", icon = Icons.Default.Home, title = "خانه")
    object AddScreen : Routes("detail_screen")

    object ExpiredPersons :
        Routes("expired_person", icon = Icons.Default.Person, title = "موعد شهریه")

    object DatePayExpired :
        Routes("date_pay_expired", icon = Icons.Default.DateRange, title = "رد شده از شهریه")

    companion object {
        val items = listOf(
            HomeScreen, AddScreen, ExpiredPersons, DatePayExpired
        )
    }
}
