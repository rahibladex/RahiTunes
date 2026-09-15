package app.rahitunes.android.ui.screens.player

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.media3.common.Player
import app.rahitunes.android.Database
import app.rahitunes.android.LocalPlayerServiceBinder
import app.rahitunes.android.R
import app.rahitunes.android.models.Info
import app.rahitunes.android.models.ui.UiMedia
import app.rahitunes.android.preferences.PlayerPreferences
import app.rahitunes.android.service.PlayerService
import app.rahitunes.android.ui.components.FadingRow
import app.rahitunes.android.ui.components.SeekBar
import app.rahitunes.android.ui.components.themed.BigIconButton
import app.rahitunes.android.ui.components.themed.IconButton
import app.rahitunes.android.ui.screens.artistRoute
import app.rahitunes.android.utils.bold
import app.rahitunes.android.utils.forceSeekToNext
import app.rahitunes.android.utils.forceSeekToPrevious
import app.rahitunes.android.utils.secondary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.favoritesIcon
import app.rahitunes.core.ui.utils.px
import app.rahitunes.core.ui.utils.roundedShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val DefaultOffset = 18.dp

@Composable
fun Controls(
    media: UiMedia?,
    binder: PlayerService.Binder?,
    likedAt: Long?,
    setLikedAt: (Long?) -> Unit,
    shouldBePlaying: Boolean,
    position: Long,
    modifier: Modifier = Modifier,
    layout: PlayerPreferences.PlayerLayout = PlayerPreferences.playerLayout
) {
    val shouldBePlayingTransition = updateTransition(
        targetState = shouldBePlaying,
        label = "shouldBePlaying"
    )

    val playButtonRadius by shouldBePlayingTransition.animateDp(
        transitionSpec = { tween(durationMillis = 150, easing = LinearEasing) },
        label = "playPauseRoundness",
        targetValueByState = { if (it) 20.dp else 34.dp }
    )

    if (media != null && binder != null) when (layout) {
        PlayerPreferences.PlayerLayout.Classic -> ClassicControls(
            media = media,
            binder = binder,
            shouldBePlaying = shouldBePlaying,
            position = position,
            likedAt = likedAt,
            setLikedAt = setLikedAt,
            playButtonRadius = playButtonRadius,
            modifier = modifier
        )

        PlayerPreferences.PlayerLayout.New -> ModernControls(
            media = media,
            binder = binder,
            shouldBePlaying = shouldBePlaying,
            position = position,
            likedAt = likedAt,
            setLikedAt = setLikedAt,
            playButtonRadius = playButtonRadius,
            modifier = modifier
        )
    }
}

@Composable
private fun ClassicControls(
    media: UiMedia,
    binder: PlayerService.Binder,
    shouldBePlaying: Boolean,
    position: Long,
    likedAt: Long?,
    setLikedAt: (Long?) -> Unit,
    playButtonRadius: Dp,
    modifier: Modifier = Modifier
) = with(PlayerPreferences) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        MediaInfo(media)
        Spacer(modifier = Modifier.weight(0.5f))

        SeekBar(
            binder = binder,
            position = position,
            media = media,
            color = Color(0xFFFF9100),
            backgroundColor = Color(0xFF151528),
            alwaysShowDuration = true
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Like Satellite
            GlassControlCircle(
                onClick = { setLikedAt(if (likedAt == null) System.currentTimeMillis() else null) },
                active = likedAt != null,
                activeColor = Color(0xFFFF1212)
            ) {
                Image(
                    painter = painterResource(if (likedAt == null) R.drawable.heart_outline else R.drawable.heart),
                    contentDescription = "Like",
                    colorFilter = ColorFilter.tint(if (likedAt != null) Color(0xFFFF1212) else Color(0xFFA2A2D0)),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Skip Back Satellite
            GlassControlCircle(
                onClick = binder.player::forceSeekToPrevious
            ) {
                Image(
                    painter = painterResource(R.drawable.play_skip_back),
                    contentDescription = "Previous",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center Glowing Master Play Dial (#FF9100 to #FF1212 Gradient Circle)
            GlowingMasterPlayButton(
                radius = playButtonRadius,
                shouldBePlaying = shouldBePlaying,
                onClick = {
                    if (shouldBePlaying) binder.player.pause()
                    else {
                        if (binder.player.playbackState == Player.STATE_IDLE) binder.player.prepare()
                        binder.player.play()
                    }
                }
            )

            // Skip Forward Satellite
            GlassControlCircle(
                onClick = binder.player::forceSeekToNext
            ) {
                Image(
                    painter = painterResource(R.drawable.play_skip_forward),
                    contentDescription = "Next",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Track Loop Satellite
            GlassControlCircle(
                onClick = { trackLoopEnabled = !trackLoopEnabled },
                active = trackLoopEnabled,
                activeColor = Color(0xFFFF9100)
            ) {
                Image(
                    painter = painterResource(R.drawable.infinite),
                    contentDescription = "Loop",
                    colorFilter = ColorFilter.tint(if (trackLoopEnabled) Color(0xFFFF9100) else Color(0xFFA2A2D0)),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))
        PlayerQuickActionsDock()
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun ModernControls(
    media: UiMedia,
    binder: PlayerService.Binder,
    shouldBePlaying: Boolean,
    position: Long,
    likedAt: Long?,
    setLikedAt: (Long?) -> Unit,
    playButtonRadius: Dp,
    modifier: Modifier = Modifier
) = with(PlayerPreferences) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        MediaInfo(media)
        Spacer(modifier = Modifier.weight(0.5f))

        SeekBar(
            binder = binder,
            position = position,
            media = media,
            color = Color(0xFFFF9100),
            backgroundColor = Color(0xFF151528),
            alwaysShowDuration = true
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Like Satellite
            GlassControlCircle(
                onClick = { setLikedAt(if (likedAt == null) System.currentTimeMillis() else null) },
                active = likedAt != null,
                activeColor = Color(0xFFFF1212)
            ) {
                Image(
                    painter = painterResource(if (likedAt == null) R.drawable.heart_outline else R.drawable.heart),
                    contentDescription = "Like",
                    colorFilter = ColorFilter.tint(if (likedAt != null) Color(0xFFFF1212) else Color(0xFFA2A2D0)),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Skip Back Satellite
            GlassControlCircle(
                onClick = binder.player::forceSeekToPrevious
            ) {
                Image(
                    painter = painterResource(R.drawable.play_skip_back),
                    contentDescription = "Previous",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center Glowing Master Play Dial (#FF9100 to #FF1212 Gradient Circle)
            GlowingMasterPlayButton(
                radius = playButtonRadius,
                shouldBePlaying = shouldBePlaying,
                onClick = {
                    if (shouldBePlaying) binder.player.pause()
                    else {
                        if (binder.player.playbackState == Player.STATE_IDLE) binder.player.prepare()
                        binder.player.play()
                    }
                }
            )

            // Skip Forward Satellite
            GlassControlCircle(
                onClick = binder.player::forceSeekToNext
            ) {
                Image(
                    painter = painterResource(R.drawable.play_skip_forward),
                    contentDescription = "Next",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Track Loop Satellite
            GlassControlCircle(
                onClick = { trackLoopEnabled = !trackLoopEnabled },
                active = trackLoopEnabled,
                activeColor = Color(0xFFFF9100)
            ) {
                Image(
                    painter = painterResource(R.drawable.infinite),
                    contentDescription = "Loop",
                    colorFilter = ColorFilter.tint(if (trackLoopEnabled) Color(0xFFFF9100) else Color(0xFFA2A2D0)),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))
        PlayerQuickActionsDock()
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun GlassControlCircle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    activeColor: Color = Color(0xFFA855F7),
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(46.dp)
            .shadow(
                elevation = if (active) 10.dp else 0.dp,
                shape = CircleShape,
                spotColor = activeColor.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                if (active) activeColor.copy(alpha = 0.15f)
                else Color(0xFF12121E).copy(alpha = 0.85f)
            )
            .border(
                1.dp,
                if (active) activeColor.copy(alpha = 0.6f)
                else Color(0xFFA855F7).copy(alpha = 0.2f),
                CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        content()
    }
}

@Composable
private fun GlowingMasterPlayButton(
    radius: Dp,
    shouldBePlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(64.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = Color(0xFFFF9100).copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFF9100), Color(0xFFFF1212))
                )
            )
            .clickable(onClick = onClick)
    ) {
        AnimatedPlayPauseButton(
            playing = shouldBePlaying,
            color = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun PlayerQuickActionsDock() {
    // Suppress quick actions dock pills
}

@Composable
private fun MediaInfo(media: UiMedia) {
    val (colorPalette, typography) = LocalAppearance.current

    var artistInfo: List<Info>? by remember { mutableStateOf(null) }
    var maxHeight by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(media) {
        withContext(Dispatchers.IO) {
            artistInfo = Database
                .songArtistInfo(media.id)
                .takeIf { it.isNotEmpty() }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(
            targetState = media.title,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = ""
        ) { title ->
            FadingRow(modifier = Modifier.fillMaxWidth(0.9f)) {
                BasicText(
                    text = title,
                    style = typography.l.bold.copy(
                        color = Color.White,
                        fontSize = 19.sp
                    ),
                    maxLines = 1
                )
            }
        }

        AnimatedContent(
            targetState = media to artistInfo,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = ""
        ) { (media, state) ->
            state?.let { artists ->
                FadingRow(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .heightIn(maxHeight.px.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    artists.fastForEachIndexed { i, artist ->
                        if (i == artists.lastIndex && artists.size > 1) BasicText(
                            text = " & ",
                            style = typography.s.semiBold.copy(color = Color(0xFF94A3B8))
                        )
                        BasicText(
                            text = artist.name.orEmpty(),
                            style = typography.s.bold.copy(color = Color(0xFF38BDF8)),
                            modifier = Modifier.clickable { artistRoute.global(artist.id) }
                        )
                        if (i != artists.lastIndex && i + 1 != artists.lastIndex) BasicText(
                            text = ", ",
                            style = typography.s.semiBold.copy(color = Color(0xFF94A3B8))
                        )
                    }
                    if (media.explicit) {
                        Spacer(Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(4.dp.roundedShape)
                                .background(Color(0xFFFF2A6D).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFFF2A6D).copy(alpha = 0.5f), 4.dp.roundedShape)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            BasicText(
                                text = "EXPLICIT",
                                style = typography.xxs.bold.copy(
                                    color = Color(0xFFFF2A6D),
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }
            } ?: FadingRow(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = media.artist,
                    style = typography.s.semiBold.copy(color = Color(0xFF38BDF8)),
                    maxLines = 1,
                    modifier = Modifier.onGloballyPositioned { maxHeight = it.size.height }
                )
                if (media.explicit) {
                    Spacer(Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(4.dp.roundedShape)
                            .background(Color(0xFFFF2A6D).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFFF2A6D).copy(alpha = 0.5f), 4.dp.roundedShape)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        BasicText(
                            text = "EXPLICIT",
                            style = typography.xxs.bold.copy(
                                color = Color(0xFFFF2A6D),
                                fontSize = 8.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
