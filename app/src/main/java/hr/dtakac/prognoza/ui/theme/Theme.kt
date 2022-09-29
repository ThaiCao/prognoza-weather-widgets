package hr.dtakac.prognoza.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import hr.dtakac.prognoza.entities.forecast.ForecastDescription

private val LocalPrognozaColors = staticCompositionLocalOf {
    PrognozaColors(
        surface = Color.Unspecified,
        onSurface = Color.Unspecified,
        moodOverlay = Color.Unspecified
    )
}

private val LocalPrognozaTypography = staticCompositionLocalOf {
    PrognozaTypography(
        headlineLarge = TextStyle.Default,
        headlineSmall = TextStyle.Default,
        titleLarge = TextStyle.Default,
        titleMedium = TextStyle.Default,
        subtitleLarge = TextStyle.Default,
        subtitleMedium = TextStyle.Default,
        body = TextStyle.Default,
        titleSmall = TextStyle.Default
    )
}

private val LocalPrognozaContentAlpha = staticCompositionLocalOf {
    PrognozaContentAlpha(
        high = 1f,
        medium = 1f
    )
}

private object PrognozaRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(
        contentColor = LocalContentColor.current,
        lightTheme = !isSystemInDarkTheme()
    )

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        contentColor = LocalContentColor.current,
        lightTheme = !isSystemInDarkTheme()
    )
}

@Composable
fun PrognozaTheme(
    description: ForecastDescription.Short = ForecastDescription.Short.UNKNOWN,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val alpha = PrognozaContentAlpha.get()
    val colors = PrognozaColors.get(
        shortForecastDescription = description,
        useDarkTheme = useDarkTheme
    )
    val typography = PrognozaTypography.get()
    CompositionLocalProvider(
        LocalPrognozaColors provides colors,
        LocalPrognozaTypography provides typography,
        LocalPrognozaContentAlpha provides alpha,
        LocalRippleTheme provides PrognozaRippleTheme,
        content = content
    )
}

object PrognozaTheme {
    val backgroundColor: Color
        @Composable
        get() = animateColorAsState(
            targetValue = LocalPrognozaColors.current.surface.applyOverlay(
                overlayColor = LocalPrognozaColors.current.moodOverlay,
                overlayAlpha = 0.12f
            ),
            animationSpec = rememberColorAnimationSpec()
        ).value

    val elevatedBackgroundColor: Color
        @Composable
        get() = animateColorAsState(
            targetValue = LocalPrognozaColors.current.surface.applyOverlay(
                overlayColor = LocalPrognozaColors.current.moodOverlay,
                overlayAlpha = 0.24f
            ),
            animationSpec = rememberColorAnimationSpec()
        ).value

    val onBackgroundColor: Color
        @Composable
        get() = animateColorAsState(
            targetValue = LocalPrognozaColors.current.onSurface.copy(alpha = alpha.high),
            animationSpec = rememberColorAnimationSpec()
        ).value

    val typography: PrognozaTypography
        @Composable
        get() = LocalPrognozaTypography.current

    val alpha: PrognozaContentAlpha
        @Composable
        get() = LocalPrognozaContentAlpha.current

    @Composable
    private fun rememberColorAnimationSpec(): AnimationSpec<Color> = remember { tween(durationMillis = 1000) }
}