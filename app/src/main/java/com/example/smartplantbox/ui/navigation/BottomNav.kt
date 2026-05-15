package com.example.smartplantbox.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.smartplantbox.R

sealed class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: Any
) {
    object Home : BottomNavItem("home", R.string.bottom_nav_home, Icons.Default.Home)
    object Stats : BottomNavItem("stats", R.string.bottom_nav_statistic, Icons.Default.DateRange)
    object Image : BottomNavItem("images", R.string.bottom_nav_images, R.drawable.ic_image)
    object Profile : BottomNavItem("profile", R.string.bottom_nav_profile, Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Stats,
        BottomNavItem.Image,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = Color(0xFF1B5E20)
    ) {
        items.forEach { item ->
            val title = stringResource(item.titleResId)

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = {
                    when (item.icon) {
                        is ImageVector -> {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = title,
                                tint = Color.White
                            )
                        }
                        is Int -> {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = title,
                                tint = Color.White
                            )
                        }
                    }
                },
                label = {
                    Text(text = title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color(0xFF4CAF50)
                )
            )
        }
    }
}