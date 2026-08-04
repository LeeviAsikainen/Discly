package com.example.udisc

import androidx.compose.ui.graphics.Color

val LightForestTheme = AppColors(
    background = Color(0xFFDAD7CD),  // #dad7cd
    card = Color(0xFFA3B18A),        // #a3b18a
    accent = Color(0xFF588157),      // #588157

    text = Color(0xFF344E41),        // #344e41
    subText = Color(0xFF3A5A40),     // #3a5a40

    ball = Color.White
)
val RoseTheme = AppColors(
    background = Color(0xFFFFE5EC),
    card = Color(0xFFFFB3C6),
    accent = Color(0xFFFB6F92),

    text = Color(0xFF590D22),
    subText = Color(0xFF800F2F),

    ball = Color.White
)

val LavenderTheme = AppColors(
    background = Color(0xFFF5EDFF),
    card = Color(0xFFE5D4FF),
    accent = Color(0xFF9F7AEA),

    text = Color(0xFF2D1B4E),
    subText = Color(0xFF6B5A99),

    ball = Color.White
)

val IceBlueTheme = AppColors(
    background = Color(0xFFEAF6FF),
    card = Color(0xFFBEE3F8),
    accent = Color(0xFF3182CE),

    text = Color(0xFF0B3C5D),
    subText = Color(0xFF4A6FA5),

    ball = Color.White
)

val DesertTheme = AppColors(
    background = Color(0xFFFFF4E6),
    card = Color(0xFFEAD2AC),
    accent = Color(0xFFD4A373),

    text = Color(0xFF5C3D2E),
    subText = Color(0xFF8C6A4A),

    ball = Color.White
)
val DarkPinkTheme = AppColors(
    background = Color(0xFF121212),
    card = Color(0xFF1E1E1E),
    accent = Color(0xFFFF4D8D),

    text = Color.White,
    subText = Color(0xFF9E9E9E),

    ball = Color.White
)
val DarkForestTheme = AppColors(
    background = Color(0xFF0B1A13),
    card = Color(0xFF13261D),
    accent = Color(0xFF4CAF50),

    text = Color.White,
    subText = Color(0xFF9FB5A7),

    ball = Color.White
)
val DarkOceanTheme = AppColors(
    background = Color(0xFF0A1A24),
    card = Color(0xFF102A3A),
    accent = Color(0xFF4DA8DA),

    text = Color.White,
    subText = Color(0xFF9EC1D9),

    ball = Color.White
)
val BlackGoldTheme = AppColors(
    background = Color(0xFF000000),   // #000000
    card = Color(0xFF14213D),         // #14213d
    accent = Color(0xFFFCA311),       // #fca311

    text = Color(0xFFFFFFFF),         // #ffffff
    subText = Color(0xFFE5E5E5),      // #e5e5e5

    ball = Color.White
)
val DarkArcticTheme = AppColors(
    background = Color(0xFF0B0F14),
    card = Color(0xFF151B22),
    accent = Color(0xFF7C858F),

    text = Color.White,
    subText = Color(0xFFB0B8C1),

    ball = Color.White
)

fun getTheme(mode: ThemeMode): AppColors {
    return when (mode) {
        ThemeMode.LIGHT_FOREST -> LightForestTheme
        ThemeMode.ROSE -> RoseTheme
        ThemeMode.LAVENDER -> LavenderTheme
        ThemeMode.ICE_BLUE -> IceBlueTheme
        ThemeMode.DESERT -> DesertTheme

        ThemeMode.DARK_PINK -> DarkPinkTheme
        ThemeMode.DARK_FOREST -> DarkForestTheme
        ThemeMode.DARK_OCEAN -> DarkOceanTheme
        ThemeMode.DARK_GOLD -> BlackGoldTheme
        ThemeMode.DARK_ARCTIC -> DarkArcticTheme
    }
}