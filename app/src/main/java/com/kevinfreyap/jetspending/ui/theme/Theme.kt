package com.kevinfreyap.jetspending.ui.theme

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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    secondary = Grey400,
    background = Grey900,
    surface = Grey900,

    secondaryContainer = Color.White.copy(alpha = 0.1f),
    onSecondaryContainer = Blue300,
    surfaceContainer = Grey1000,
    onSurfaceVariant = Color.White.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    secondary = Grey700,
    background = OffWhite,
    surface = OffWhite,

    secondaryContainer = Blue500.copy(alpha = 0.1f),
    onSecondaryContainer = Blue700,
    surfaceContainer = Blue100.copy(alpha = 0.3f),
    onSurfaceVariant = Grey400
)

@Immutable
data class CustomColors(
    val cardColor: Color,
    val nestedCardColor: Color,
    val textColor: Color,
    val iconColor: Color,
    val hintColor: Color,
)

// Define the default/fallback
val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        cardColor = Color.Unspecified,
        nestedCardColor = Color.Unspecified,
        textColor = Color.Unspecified,
        iconColor = Color.Unspecified,
        hintColor = Color.Unspecified,
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

    val customColors = if (darkTheme) {
        CustomColors(
            cardColor = Grey1000,
            nestedCardColor = Grey950,
            textColor = OffWhite,
            iconColor = Grey400,
            hintColor = Color.White.copy(alpha = 0.2f),
        )
    } else {
        CustomColors(
            cardColor = Grey200.copy(alpha = 0.5f),
            nestedCardColor = Grey200,
            textColor = Grey900,
            iconColor = Grey800,
            hintColor = Grey400,
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