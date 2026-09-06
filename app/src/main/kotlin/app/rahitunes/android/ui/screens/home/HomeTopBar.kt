package app.rahitunes.android.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.android.utils.bold
import app.rahitunes.android.utils.color
import app.rahitunes.android.utils.medium
import app.rahitunes.core.ui.LocalAppearance

@Composable
fun HomeTopBar(
    userName: String = "Alex",
    selectedFilter: String,
    filterOptions: List<String> = listOf("⚡ All Tracks", "🔥 Viral 50", "🌌 Cyberpunk", "🌊 Chill Lo-Fi", "🎧 Deep Focus", "✨ Fresh Drops"),
    onFilterSelected: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 8.dp)
    ) {
        // Top Command Row: Logo + Live Audio Spectrum + User Avatar + Quick Favorites
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: User Avatar & Holographic Branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clickable(onClick = onAvatarClick)
            ) {
                // Neon Ring Profile Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFFE08000).copy(alpha = 0.5f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFE08000),
                                    Color(0xFFE04000),
                                    Color(0xFFE06000),
                                    Color(0xFFE08000)
                                )
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF07080D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.person),
                            contentDescription = "Profile",
                            colorFilter = ColorFilter.tint(Color(0xFFE08000)),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    BasicText(
                        text = "RAHITUNES",
                        style = typography.s.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontSize = 14.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MusicBars(
                            color = Color(0xFFE08000),
                            modifier = Modifier.size(12.dp, 9.dp)
                        )
                        BasicText(
                            text = "Hi-Fi Audio",
                            maxLines = 1,
                            style = typography.xxs.copy(
                                color = Color(0xFF8E9AA8),
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Right: Favorites Action Pill
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color(0xFFE04000).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF131726))
                    .border(1.dp, Color(0xFFE04000).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onFavoritesClick)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.heart),
                        contentDescription = "Favorites",
                        colorFilter = ColorFilter.tint(Color(0xFFE04000)),
                        modifier = Modifier.size(13.dp)
                    )
                    BasicText(
                        text = "Favorites",
                        maxLines = 1,
                        style = typography.xxs.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Large Holographic Interactive Search Capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = Color(0xFFE08000).copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0F121C), Color(0xFF181E2E))
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFE08000).copy(alpha = 0.6f),
                            Color(0xFFE04000).copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(onClick = onSearchClick)
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Image(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search",
                        colorFilter = ColorFilter.tint(Color(0xFFE08000)),
                        modifier = Modifier.size(17.dp)
                    )
                    BasicText(
                        text = "Search songs, albums, artists...",
                        maxLines = 1,
                        style = typography.xs.copy(
                            color = Color(0xFF8E9AA8),
                            fontSize = 12.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE08000).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFE08000).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    BasicText(
                        text = "FLAC 24-BIT",
                        maxLines = 1,
                        style = typography.xxs.copy(
                            color = Color(0xFFE08000),
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Horizontal Category Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            filterOptions.forEach { filter ->
                val isSelected = filter == selectedFilter

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFFE08000) else Color(0xFF11141D),
                    label = "filter_bg"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF07080D) else Color(0xFF8E9AA8),
                    label = "filter_text"
                )

                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color(0xFFE08000).copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(backgroundColor)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFFE08000) else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onFilterSelected(filter) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = filter,
                        style = if (isSelected) typography.xs.bold.color(textColor) else typography.xs.medium.color(textColor)
                    )
                }
            }
        }
    }
}
