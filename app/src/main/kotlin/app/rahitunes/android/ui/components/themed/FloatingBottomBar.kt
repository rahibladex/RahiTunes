package app.rahitunes.android.ui.components.themed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import app.rahitunes.core.ui.Dimensions
import app.rahitunes.core.ui.LocalAppearance
import kotlinx.collections.immutable.ImmutableList

@Composable
fun FloatingBottomBar(
    tabs: ImmutableList<Tab>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.bottomBar.horizontalMargin,
                vertical = Dimensions.bottomBar.bottomMargin
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(36.dp),
                    spotColor = Color(0xFF00F0FF).copy(alpha = 0.15f),
                    ambientColor = Color.Black
                )
                .clip(RoundedCornerShape(36.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF131726).copy(alpha = 0.95f),
                            Color(0xFF0A0D15).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF00F0FF).copy(alpha = 0.35f),
                            Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.08f),
                            Color(0xFFFF0055).copy(alpha = 0.25f)
                        )
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.fastForEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.06f else 0.95f,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
                    label = "tab_scale"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF07080D) else Color(0xFF8E9AA8),
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "tab_icon_color"
                )

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        // Glowing Cyber-Circle for active tab
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(
                                    elevation = 10.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0xFF00F0FF).copy(alpha = 0.8f)
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF00F0FF),
                                            Color(0xFF38BDF8)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(tab.icon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color(0xFF07080D)),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        // Inactive icon state with subtle hover feel
                        Box(
                            modifier = Modifier
                                .size(38.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(tab.icon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(iconColor),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
