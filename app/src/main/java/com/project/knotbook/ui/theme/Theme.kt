package com.project.knotbook.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val KnotBookColorScheme = lightColorScheme(
    primary = HotPink,
    onPrimary = SurfaceWhite,
    secondary = Teal,
    onSecondary = Cocoa,
    tertiary = Tangerine,
    background = Cream,
    onBackground = Cocoa,
    surface = SurfaceWhite,
    onSurface = Cocoa,
    error = ErrorRed
)


@Composable
fun KnotBookTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = KnotBookColorScheme,
        typography = KnotBookTypography,
        content = content
    )
}