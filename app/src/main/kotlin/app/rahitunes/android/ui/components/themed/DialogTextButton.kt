package app.rahitunes.android.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.rahitunes.android.utils.color
import app.rahitunes.android.utils.disabled
import app.rahitunes.android.utils.medium
import app.rahitunes.android.utils.primary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.utils.roundedShape

@Composable
fun DialogTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false
) {
    val (colorPalette, typography) = LocalAppearance.current

    BasicText(
        text = text,
        style = typography.xs.semiBold.let {
            when {
                !enabled -> it.disabled
                primary -> it.color(colorPalette.onAccent)
                else -> it.color(colorPalette.textSecondary)
            }
        },
        modifier = modifier
            .clip(36.dp.roundedShape)
            .background(if (primary) colorPalette.accent else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    )
}
