package com.aa.usermanagementapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Neutral99,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary = BlueGrey40,
    onSecondary = Neutral99,
    secondaryContainer = BlueGrey90,
    onSecondaryContainer = BlueGrey30,

    tertiary = Teal40,
    onTertiary = Neutral99,

    error = Red40,
    onError = Neutral99,

    background = Neutral99,
    onBackground = Neutral10,

    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,

    outline = NeutralVariant50,
    outlineVariant = NeutralVariant90,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,

    secondary = BlueGrey80,
    onSecondary = BlueGrey30,
    secondaryContainer = BlueGrey40,
    onSecondaryContainer = BlueGrey90,

    tertiary = Teal80,
    onTertiary = Neutral10,

    error = Red80,
    onError = Red40,

    background = Neutral10,
    onBackground = Neutral90,

    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant60,

    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
)

@Composable
fun UserManagementAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}