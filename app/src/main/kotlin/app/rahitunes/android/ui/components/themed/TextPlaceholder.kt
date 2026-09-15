package app.rahitunes.android.ui.components.themed

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.shimmer

@Composable
fun TextPlaceholder(
    modifier: Modifier = Modifier,
    color: Color = LocalAppearance.current.colorPalette.shimmer,
    @FloatRange(from = 0.0, to = 1.0)
    width: Float = 0.5f
) {
    // Suppress text loading placeholders
}
