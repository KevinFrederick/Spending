package com.kevinfreyap.jetspending.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    background = DarkBackground,
    surface = DarkSurface,

    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    surfaceContainer = DarkSurfaceContainer,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    background = LightBackground,
    surface = LightSurface,

    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    surfaceContainer = LightSurfaceContainer,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

@Immutable
data class CustomColors(
    val cardColor: Color,
    val nestedCardColor: Color,
    val textColor: Color,
    val iconColor: Color,
    val hintColor: Color,
    val onBoardCardColor: Color,

    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val highlightContainer: Color,
    val statusPositive: Color,
    val statusNegative: Color,
    val negativeContainer: Color,
    val statusWarning: Color,
    val statusOnWarning: Color,
    val shimmerColor: Color,
    val highlightShimmerColor: Color
)

// Define the default/fallback
val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        cardColor = Color.Unspecified,
        nestedCardColor = Color.Unspecified,
        textColor = Color.Unspecified,
        iconColor = Color.Unspecified,
        hintColor = Color.Unspecified,
        onBoardCardColor = Color.Unspecified,

        primaryTextColor = Color.Unspecified,
        secondaryTextColor = Color.Unspecified,
        highlightContainer = Color.Unspecified,
        statusPositive = Color.Unspecified,
        statusNegative = Color.Unspecified,
        negativeContainer = Color.Unspecified,
        statusWarning = Color.Unspecified,
        statusOnWarning = Color.Unspecified,
        shimmerColor = Color.Unspecified,
        highlightShimmerColor = Color.Unspecified
    )
}

@Composable
fun JetSpendingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val customColors = if (darkTheme) {
        CustomColors(
            cardColor = Grey1000,
            nestedCardColor = Grey950,
            textColor = OffWhite,
            iconColor = Grey400,
            hintColor = Color.White.copy(alpha = 0.2f),
            onBoardCardColor = Blue500.copy(alpha = 0.9f),

            primaryTextColor = DarkPrimaryText,
            secondaryTextColor = DarkSecondaryText,
            highlightContainer = DarkHighlightContainer,
            statusPositive = DarkStatusPositive,
            statusNegative = DarkStatusNegative,
            negativeContainer = DarkNegativeContainer,
            statusWarning = DarkStatusWarning,
            statusOnWarning = DarkStatusOnWarning,
            shimmerColor = DarkShimmer,
            highlightShimmerColor = DarkHighlightShimmer
        )
    } else {
        CustomColors(
            cardColor = Grey200.copy(alpha = 0.5f),
            nestedCardColor = Grey200,
            textColor = Grey900,
            iconColor = Grey800,
            hintColor = Grey400,
            onBoardCardColor = Blue200.copy(alpha = 0.8f),

            primaryTextColor = LightPrimaryText,
            secondaryTextColor = LightSecondaryText,
            highlightContainer = LightHighlightContainer,
            statusPositive = LightStatusPositive,
            statusNegative = LightStatusNegative,
            negativeContainer = LightNegativeContainer,
            statusWarning = LightStatusWarning,
            statusOnWarning = LightStatusOnWarning,
            shimmerColor = LightShimmer,
            highlightShimmerColor = LightHighlightShimmer
        )
    }

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object Theme {
    val custom: CustomColors
        @Composable
        get() = LocalCustomColors.current
}