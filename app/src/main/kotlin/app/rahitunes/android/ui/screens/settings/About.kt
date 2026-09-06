package app.rahitunes.android.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.BuildConfig
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.core.ui.LocalAppearance
import coil3.compose.AsyncImage

private const val GITHUB_PROFILE_URL = "https://github.com/rahibladex"
private const val PORTFOLIO_URL = "https://rahibladex-portfolio.vercel.app/"
private const val EMAIL_URL = "mailto:rahibladex@gmail.com"
private const val AVATAR_URL = "https://avatars.githubusercontent.com/u/132987103?v=4"

@Composable
fun About() = SettingsCategoryScreen(
    title = stringResource(R.string.about),
    description = "Meet the developer behind RahiTunes and explore the open-source ecosystem."
) {
    val (colorPalette, typography) = LocalAppearance.current
    val uriHandler = LocalUriHandler.current

    // 1. DEVELOPER HERO CARD (Rahul Jangra / RAHIBLADEX)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF00F0FF).copy(alpha = 0.25f),
                ambientColor = Color.Black
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F1424),
                        Color(0xFF181C2E),
                        Color(0xFF0D121F)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF00F0FF).copy(alpha = 0.8f),
                        Color(0xFF8B5CF6).copy(alpha = 0.4f),
                        Color(0xFFFF0055).copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Neon Profile Avatar with dynamic GitHub photo or fallback
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF00F0FF),
                                    Color(0xFFFF0055),
                                    Color(0xFFF59E0B),
                                    Color(0xFF8B5CF6),
                                    Color(0xFF00F0FF)
                                )
                            )
                        )
                        .padding(2.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(63.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF07080D)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = AVATAR_URL,
                            contentDescription = "Rahul Jangra",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(63.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BasicText(
                            text = "Rahul Jangra",
                            style = typography.l.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00F0FF).copy(alpha = 0.15f))
                                .border(0.8.dp, Color(0xFF00F0FF).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            BasicText(
                                text = "@rahibladex",
                                style = typography.xxs.copy(
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                                .border(0.8.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            BasicText(
                                text = "🇮🇳 India",
                                style = typography.xxs.copy(
                                    color = Color(0xFFF59E0B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    BasicText(
                        text = "AI/ML Engineer • BCA Student",
                        style = typography.xs.copy(
                            color = Color(0xFF00F0FF),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Bio description
            BasicText(
                text = "Student of BCA with specialization in AI/ML from India. Creator of RahiTunes, AcousticGuard, and intelligent digital twin systems. Passionate about building high-performance, futuristic Android experiences and 100% free open-source software.",
                style = typography.xs.copy(
                    color = Color(0xFFC0CAD8),
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            )

            Spacer(Modifier.height(16.dp))

            // Action Pills (GitHub, Portfolio, Email)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // GitHub Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00F0FF), Color(0xFF0099FF))
                            )
                        )
                        .clickable { uriHandler.openUri(GITHUB_PROFILE_URL) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.globe),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFF07080D)),
                            modifier = Modifier.size(16.dp)
                        )
                        BasicText(
                            text = "GitHub",
                            style = typography.xs.copy(
                                color = Color(0xFF07080D),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Portfolio Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141828))
                        .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { uriHandler.openUri(PORTFOLIO_URL) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.sparkles),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFF8B5CF6)),
                            modifier = Modifier.size(16.dp)
                        )
                        BasicText(
                            text = "Portfolio",
                            style = typography.xs.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Email Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141828))
                        .border(1.dp, Color(0xFFFF0055).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { uriHandler.openUri(EMAIL_URL) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.person),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFFFF0055)),
                            modifier = Modifier.size(16.dp)
                        )
                        BasicText(
                            text = "Contact",
                            style = typography.xs.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }

    SettingsGroupSpacer()

    // 2. FEATURED ECOSYSTEM PROJECTS
    SettingsGroup(title = "Featured Projects by Rahul Jangra") {
        ProjectCard(
            title = "RahiTunes",
            subtitle = "Futuristic Cyber-Neon Hi-Fi Music Streaming Engine",
            tag = "Music & Audio",
            tagColor = Color(0xFF00F0FF),
            iconRes = R.drawable.musical_notes,
            onClick = { uriHandler.openUri(GITHUB_PROFILE_URL) }
        )

        ProjectCard(
            title = "AcousticGuard",
            subtitle = "Intelligent Acoustic Monitoring & Threat Detection Platform",
            tag = "Kotlin / AI",
            tagColor = Color(0xFFFF0055),
            iconRes = R.drawable.equalizer,
            onClick = { uriHandler.openUri("https://github.com/rahibladex/AcousticGuard") }
        )

        ProjectCard(
            title = "Cortex-City",
            subtitle = "Predictive Urban Mobility & Traffic Digital Twin Simulator",
            tag = "Simulation / AI",
            tagColor = Color(0xFF8B5CF6),
            iconRes = R.drawable.globe,
            onClick = { uriHandler.openUri("https://github.com/rahibladex/Cortex-City") }
        )
    }

    SettingsGroupSpacer()

    // 3. APP INFO & OPEN SOURCE PHILOSOPHY
    SettingsGroup(title = "Application Specifications") {
        SettingsEntry(
            title = "RahiTunes Version",
            text = "${BuildConfig.VERSION_NAME} • Free & Open Source Master Edition",
            trailingContent = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00F0FF).copy(alpha = 0.15f))
                        .border(0.8.dp, Color(0xFF00F0FF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    BasicText(
                        text = "100% FREE",
                        style = typography.xxs.copy(
                            color = Color(0xFF00F0FF),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    )
                }
            },
            onClick = {}
        )

        SettingsEntry(
            title = "Developer GitHub Profile",
            text = "https://github.com/rahibladex",
            onClick = { uriHandler.openUri(GITHUB_PROFILE_URL) }
        )

        SettingsEntry(
            title = "Portfolio Website",
            text = "https://rahibladex-portfolio.vercel.app/",
            onClick = { uriHandler.openUri(PORTFOLIO_URL) }
        )

        SettingsEntry(
            title = "Direct Inquiries",
            text = "rahibladex@gmail.com",
            onClick = { uriHandler.openUri(EMAIL_URL) }
        )
    }
}

@Composable
private fun ProjectCard(
    title: String,
    subtitle: String,
    tag: String,
    tagColor: Color,
    iconRes: Int,
    onClick: () -> Unit
) {
    val (_, typography) = LocalAppearance.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF111420), Color(0xFF161B2C))
                )
            )
            .border(
                width = 1.dp,
                color = tagColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(tagColor.copy(alpha = 0.12f))
                    .border(1.dp, tagColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(tagColor),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BasicText(
                        text = title,
                        style = typography.m.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tagColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 1.5.dp)
                    ) {
                        BasicText(
                            text = tag,
                            style = typography.xxs.copy(
                                color = tagColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                BasicText(
                    text = subtitle,
                    style = typography.xs.copy(
                        color = Color(0xFF8E9AA8),
                        fontSize = 11.5.sp
                    )
                )
            }

            Image(
                painter = painterResource(R.drawable.chevron_forward),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color(0xFF8E9AA8)),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
