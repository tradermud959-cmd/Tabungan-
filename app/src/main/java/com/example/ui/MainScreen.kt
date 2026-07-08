package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BackgroundDark
import com.example.viewmodel.TabungkuViewModel
import androidx.navigation.NavController
import com.example.navigation.Screen

@Composable
fun MainScreen(viewModel: TabungkuViewModel, navController: NavController) {
    var showFloatingMenu by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(0) } // 0: Home, 1: Riwayat, 2: Settings, 3: Info

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        // Main Content with Blur
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (showFloatingMenu) 10.dp else 0.dp)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> RiwayatScreen(viewModel = viewModel)
                2 -> SettingsScreen(viewModel = viewModel)
                3 -> InfoScreen()
            }
        }

        // Overlay for Floating Menu
        if (showFloatingMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Keep the background overlay logic consistent
                    .clickable { showFloatingMenu = false }
            )
        }

        // Floating Menu
        AnimatedVisibility(
            visible = showFloatingMenu,
            enter = scaleIn(tween(300)) + fadeIn(tween(300)),
            exit = scaleOut(tween(300)) + fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            FloatingMenu(
                onDismiss = { showFloatingMenu = false },
                onNavigateUtama = {
                    showFloatingMenu = false
                    navController.navigate(Screen.TabunganUtama.route)
                },
                onNavigateTarget = {
                    showFloatingMenu = false
                    navController.navigate(Screen.TabunganTarget.route)
                }
            )
        }

        // Bottom Navigation
        CustomBottomNavigation(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            onMulaiClick = { showFloatingMenu = !showFloatingMenu },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
