package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val VarzeaBlue = Color(0xFF00B4D8)
val VarzeaDarkBlue = Color(0xFF001D4A)
val VarzeaCyan = Color(0xFF00E5FF)
val VarzeaRed = Color(0xFFDC2626)
val VarzeaGold = Color(0xFFFFD600)
val VarzeaDarkSurface = Color(0xFF0D1117)
val VarzeaCardBg = Color(0xFF161B22)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = VarzeaCyan,
    onPrimary = Color.Black,
    secondary = VarzeaBlue,
    onSecondary = Color.White,
    tertiary = VarzeaGold,
    background = VarzeaDarkSurface,
    surface = VarzeaCardBg,
    onBackground = Color.White,
    onSurface = Color.White
)
