package com.nofy.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NofyColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    surface = Surface,
    background = Background,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = Outline
)

@Composable
fun NofyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NofyColorScheme,
        typography = NofyTypography,
        content = content
    )
}
