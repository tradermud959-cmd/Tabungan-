package com.example.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.SplashScreen
import com.example.ui.MainScreen
import com.example.viewmodel.TabungkuViewModel

@Composable
fun AppNavigation(activity: FragmentActivity, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: TabungkuViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                activity = activity,
                onSplashFinished = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Main.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500)) }
        ) {
            MainScreen(viewModel = viewModel, navController = navController)
        }
        composable(
            route = Screen.TabunganUtama.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) }
        ) {
            com.example.ui.TabunganUtamaScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.TabunganTarget.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) }
        ) {
            com.example.ui.TabunganTargetScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
