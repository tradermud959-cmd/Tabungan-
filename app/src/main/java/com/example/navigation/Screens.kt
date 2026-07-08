package com.example.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object TabunganUtama : Screen("tabungan_utama")
    object TabunganTarget : Screen("tabungan_target")
    object BiometricPrompt : Screen("biometric_prompt")
}
