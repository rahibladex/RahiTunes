package app.rahitunes.android.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.core.ui.LocalAppearance
import coil3.compose.AsyncImage

data class HeroCardItem(
    val id: String,
    val badge: String,
    val hiResText: String = "LOSSLESS MASTER",
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null,
    val gradientColors: List<Color> = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))
)

@Composable
fun HomeHeroBanner(
    items: List<HeroCardItem> = listOf(
        HeroCardItem(
            id = "aurora_mix",
            badge = "🔥 TODAY'S SONIC WAVE",
            hiResText = "FLAC 96kHz",
            title = "Midnight\nAurora Wave",
            subtitle = "Hyper-curated synthwave, electronic & chill tracks",
            gradientColors = listOf(Color(0xFF1E1B30), Color(0xFF12121E), Color(0xFF08080F))
        ),
        HeroCardItem(
            id = "cyber_velocity",
            badge = "⚡ GLOBAL RADAR",
            hiResText = "TOP VIRAL 50",
            title = "Cyber\nVelocity Charts",
            subtitle = "The highest velocity trending hits right now",
            gradientColors = listOf(Color(0xFF12121E), Color(0xFF1E1B30), Color(0xFF08080F))
        )
    ),
    onPlayClick: (HeroCardItem) -> Unit,
    onLikeClick: (HeroCardItem) -> Unit = {},
    onDownloadClick: (HeroCardItem) -> Unit = {},
    onMoreClick: (HeroCardItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Section Title: "FEATURED FLOW"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFA855F7), Color(0xFF06B6D4))
                            )
                        )
                )
                BasicText(
                    text = "Sonic Stream Deck",
                    style = typography.m.copy(
                        color = Color(0xFFF0EEFF),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MusicBars(
                    color = Color(0xFFA855F7),
                    modifier = Modifier.size(14.dp, 10.dp)
                )
                BasicText(
                    text = "Master Hi-Res",
                    style = typography.xxs.copy(
                        color = Color(0xFFA855F7),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            items(items, key = { it.id }) { item ->
                Box(
                    modifier = Modifier
                        .width(310.dp)
                        .height(180.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0xFFA855F7).copy(alpha = 0.4f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(item.gradientColors)
                        )
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFA855F7).copy(alpha = 0.6f),
                                    Color(0xFF06B6D4).copy(alpha = 0.35f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    // Right-side artistic image decoration / cutout
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(130.dp)
                    ) {
                        if (!item.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                            )
                        } else {
                            // Holographic music waves
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.musical_notes),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color(0xFFA855F7).copy(alpha = 0.25f)),
                                    modifier = Modifier.size(90.dp)
                                )
                            }
                        }
                    }

                    // Left-side content: Badges, Title, Subtitle, and Controls
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Badges Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFA855F7).copy(alpha = 0.18f))
                                        .border(1.dp, Color(0xFFA855F7).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    BasicText(
                                        text = item.badge,
                                        style = typography.xxs.copy(
                                            color = Color(0xFFA855F7),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    BasicText(
                                        text = item.hiResText,
                                        style = typography.xxs.copy(
                                            color = Color(0xFFC4BDE8),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            BasicText(
                                text = item.title,
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    lineHeight = 21.sp
                                )
                            )

                            Spacer(Modifier.height(4.dp))

                            BasicText(
                                text = item.subtitle,
                                style = typography.xxs.copy(
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.5.sp,
                                    lineHeight = 13.sp
                                )
                            )
                        }

                        // Controls Row: Play Pill Button, Heart, Download
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Electric Cyan/Pink Play Pill Button
                            Box(
                                modifier = Modifier
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        spotColor = Color(0xFF00F0FF).copy(alpha = 0.5f)
                                    )
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF00F0FF), Color(0xFFFF0055))
                                        )
                                    )
                                    .clickable { onPlayClick(item) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.play),
                                        contentDescription = "Play",
                                        colorFilter = ColorFilter.tint(Color(0xFF07080D)),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    BasicText(
                                        text = "Stream",
                                        style = typography.xs.copy(
                                            color = Color(0xFF07080D),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            // Favorite Icon Button
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0C0F18))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                    .clickable { onLikeClick(item) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.heart_outline),
                                    contentDescription = "Favorite",
                                    colorFilter = ColorFilter.tint(Color(0xFFFF0055)),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Download Icon Button
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0C0F18))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                    .clickable { onDownloadClick(item) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.download),
                                    contentDescription = "Download",
                                    colorFilter = ColorFilter.tint(Color(0xFF8E9AA8)),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
