package com.example.smartplantbox.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.smartplantbox.ui.navigation.BottomNavigationBar
import com.example.smartplantbox.ui.main.home.HomeScreen
import com.example.smartplantbox.ui.main.image.ImageScreen
import com.example.smartplantbox.ui.main.profile.ProfileScreen
import com.example.smartplantbox.ui.main.stats.StatsScreen

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToPotSettings: (String, String) -> Unit
) {
    var currentRoute by remember { mutableStateOf("home") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { currentRoute = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentRoute) {
                "home" -> HomeScreen(
                    onNavigateToPotSettings = onNavigateToPotSettings
                )
                "stats" -> StatsScreen()
                "images" -> ImageScreen()
                "profile" -> ProfileScreen(
                    onLogoutClick = onLogout
                )
                else -> HomeScreen(
                    onNavigateToPotSettings = onNavigateToPotSettings
                )
            }
        }
    }
}