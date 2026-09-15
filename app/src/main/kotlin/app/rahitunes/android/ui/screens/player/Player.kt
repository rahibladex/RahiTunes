package app.rahitunes.android.ui.screens.player

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import app.rahitunes.android.Database
import app.rahitunes.android.LocalPlayerServiceBinder
import app.rahitunes.android.R
import app.rahitunes.android.models.ui.toUiMedia
import app.rahitunes.android.preferences.PlayerPreferences
import app.rahitunes.android.query
import app.rahitunes.android.service.PlayerService
import app.rahitunes.android.transaction
import app.rahitunes.android.ui.components.BottomSheet
import app.rahitunes.android.ui.components.BottomSheetState
import app.rahitunes.android.ui.components.LocalMenuState
import app.rahitunes.android.ui.components.rememberBottomSheetState
import app.rahitunes.android.ui.components.themed.BaseMediaItemMenu
import app.rahitunes.android.ui.components.themed.IconButton
import app.rahitunes.android.ui.components.themed.SecondaryTextButton
import app.rahitunes.android.ui.components.themed.SliderDialog
import app.rahitunes.android.ui.components.themed.SliderDialogBody
import app.rahitunes.android.ui.modifiers.PinchDirection
import app.rahitunes.android.ui.modifiers.onSwipe
import app.rahitunes.android.ui.modifiers.pinchToToggle
import app.rahitunes.android.utils.DisposableListener
import app.rahitunes.android.utils.Pip
import app.rahitunes.android.utils.forceSeekToNext
import app.rahitunes.android.utils.forceSeekToPrevious
import app.rahitunes.android.utils.positionAndDurationState
import app.rahitunes.android.utils.rememberEqualizerLauncher
import app.rahitunes.android.utils.rememberPipHandler
import app.rahitunes.android.utils.seamlessPlay
import app.rahitunes.android.utils.secondary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.android.utils.shouldBePlaying
import app.rahitunes.android.utils.thumbnail
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.routing.OnGlobalRoute
import app.rahitunes.core.ui.Dimensions
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.ThumbnailRoundness
import app.rahitunes.core.ui.collapsedPlayerProgressBar
import app.rahitunes.core.ui.utils.isLandscape
import app.rahitunes.core.ui.utils.px
import app.rahitunes.core.ui.utils.roundedShape
import app.rahitunes.core.ui.utils.songBundle
import app.rahitunes.providers.innertube.models.NavigationEndpoint
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

import androidx.compose.ui.unit.sp
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.android.utils.bold

@Composable
fun Player(
    layoutState: BottomSheetState,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp
    ),
    windowInsets: WindowInsets = WindowInsets.systemBars
) = with(PlayerPreferences) {
    val menuState = LocalMenuState.current
    val (colorPalette, typography, thumbnailCornerSize) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current

    val pipHandler = rememberPipHandler()

    PersistMapCleanup(prefix = "queue/suggestions")

    var mediaItem by remember(binder) {
        mutableStateOf(
            value = binder?.player?.currentMediaItem,
            policy = neverEqualPolicy()
        )
    }
    var shouldBePlaying by remember(binder) { mutableStateOf(binder?.player?.shouldBePlaying == true) }

    var likedAt by remember(mediaItem) {
        mutableStateOf(
            value = null,
            policy = object : SnapshotMutationPolicy<Long?> {
                override fun equivalent(a: Long?, b: Long?): Boolean {
                    mediaItem?.mediaId?.let {
                        query {
                            Database.like(it, b)
                        }
                    }
                    return a == b
                }
            }
        )
    }

    LaunchedEffect(mediaItem) {
        mediaItem?.mediaId?.let { mediaId ->
            Database
                .likedAt(mediaId)
                .distinctUntilChanged()
                .collect { likedAt = it }
        }
    }

    binder?.player.DisposableListener {
        object : Player.Listener {
            override fun onMediaItemTransition(newMediaItem: MediaItem?, reason: Int) {
                mediaItem = newMediaItem
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                shouldBePlaying = player.shouldBePlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                shouldBePlaying = player.shouldBePlaying
            }
        }
    }

    val (position, duration) = binder?.player.positionAndDurationState()
    val metadata = remember(mediaItem) { mediaItem?.mediaMetadata }
    val extras = remember(metadata) { metadata?.extras?.songBundle }

    val horizontalBottomPaddingValues = windowInsets
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
        .asPaddingValues()

    OnGlobalRoute { if (layoutState.expanded) layoutState.collapseSoft() }

    if (mediaItem != null) BottomSheet(
        state = layoutState,
        modifier = modifier.fillMaxSize(),
        onDismiss = {
            binder?.let { onDismiss(it) }
            layoutState.dismissSoft()
        },
        backHandlerEnabled = !menuState.isDisplayed,
        collapsedContent = { innerModifier ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(Dimensions.items.collapsedPlayerHeight)
                        .shadow(
                            elevation = 16.dp,
                            shape = 20.dp.roundedShape,
                            spotColor = Color(0xFFFF9100).copy(alpha = 0.25f)
                        )
                        .clip(20.dp.roundedShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF12121E), Color(0xFF151528))
                            )
                        )
                        .border(1.2.dp, Color(0xFFFF9100).copy(alpha = 0.35f), 20.dp.roundedShape)
                        .drawBehind {
                            val progressWidth = runCatching {
                                size.width * (position.toFloat() / duration.absoluteValue)
                            }.getOrElse { 0f }
                            drawRect(
                                color = Color(0xFFFF9100),
                                topLeft = Offset(0f, size.height - 3f.dp.toPx()),
                                size = Size(
                                    width = progressWidth,
                                    height = 3f.dp.toPx()
                                )
                            )
                        }
                        .then(innerModifier)
                        .padding(horizontal = 8.dp)
                        .let { modifier ->
                            if (horizontalSwipeToClose) modifier.onSwipe(
                                animateOffset = true,
                                onSwipeOut = { animationJob ->
                                    binder?.let { onDismiss(it) }
                                    animationJob.join()
                                    layoutState.dismissSoft()
                                }
                            ) else modifier
                        }
                ) {
                    Spacer(modifier = Modifier.width(2.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(Dimensions.items.collapsedPlayerHeight)
                    ) {
                        AsyncImage(
                            model = metadata?.artworkUri?.thumbnail(Dimensions.thumbnails.song.px),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(thumbnailCornerSize.coerceAtMost(ThumbnailRoundness.Heavy.dp).roundedShape)
                                .background(colorPalette.background0)
                                .size(44.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .height(Dimensions.items.collapsedPlayerHeight)
                            .weight(1f)
                    ) {
                        AnimatedContent(
                            targetState = metadata?.title?.toString().orEmpty(),
                            label = "",
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { text ->
                            BasicText(
                                text = text,
                                style = typography.xs.semiBold.copy(color = Color.White),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        AnimatedVisibility(visible = metadata?.artist != null) {
                            AnimatedContent(
                                targetState = metadata?.artist?.toString().orEmpty(),
                                label = "",
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { text ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    BasicText(
                                        text = text,
                                        style = typography.xs.semiBold.copy(color = Color(0xFFA2A2D0)),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    AnimatedVisibility(visible = extras?.explicit == true) {
                                        Image(
                                            painter = painterResource(R.drawable.explicit),
                                            contentDescription = null,
                                            colorFilter = ColorFilter.tint(Color(0xFFFF1212)),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(Dimensions.items.collapsedPlayerHeight)
                    ) {
                        AnimatedVisibility(visible = isShowingPrevButtonCollapsed) {
                            IconButton(
                                icon = R.drawable.play_skip_back,
                                color = Color.White,
                                onClick = { binder?.player?.forceSeekToPrevious() },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 8.dp)
                                    .size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        if (shouldBePlaying) binder?.player?.pause()
                                        else {
                                            if (binder?.player?.playbackState == Player.STATE_IDLE) binder.player.prepare()
                                            binder?.player?.play()
                                        }
                                    },
                                    indication = ripple(bounded = false),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .clip(CircleShape)
                        ) {
                            AnimatedPlayPauseButton(
                                playing = shouldBePlaying,
                                color = Color(0xFFFF9100),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 4.dp, vertical = 8.dp)
                                    .size(24.dp)
                            )
                        }

                        IconButton(
                            icon = R.drawable.play_skip_forward,
                            color = Color.White,
                            onClick = { binder?.player?.forceSeekToNext() },
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                .size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))
                }
            }
        }
    ) {
        var isShowingStatsForNerds by rememberSaveable { mutableStateOf(false) }
        var isShowingLyricsDialog by rememberSaveable { mutableStateOf(false) }
        var audioDialogOpen by rememberSaveable { mutableStateOf(false) }
        var boostDialogOpen by rememberSaveable { mutableStateOf(false) }

        if (isShowingLyricsDialog) LyricsDialog(onDismiss = { isShowingLyricsDialog = false })

        val playerBottomSheetState = rememberBottomSheetState(
            dismissedBound = 64.dp + horizontalBottomPaddingValues.calculateBottomPadding(),
            expandedBound = layoutState.expandedBound
        )

        val containerModifier = Modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    0.0f to Color(0xFF0A0D18),
                    0.35f to Color(0xFF101524),
                    0.75f to Color(0xFF0A0D18),
                    1f to Color(0xFF05070D)
                )
            )
            .padding(
                windowInsets
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            )
            .padding(bottom = playerBottomSheetState.collapsedBound)

        val thumbnailContent: @Composable (modifier: Modifier) -> Unit = { innerModifier ->
            Pip(
                numerator = 1,
                denominator = 1,
                modifier = innerModifier
            ) {
                Thumbnail(
                    isShowingLyrics = isShowingLyrics,
                    onShowLyrics = { isShowingLyrics = it },
                    isShowingStatsForNerds = isShowingStatsForNerds,
                    onShowStatsForNerds = { isShowingStatsForNerds = it },
                    onOpenDialog = { isShowingLyricsDialog = true },
                    likedAt = likedAt,
                    setLikedAt = { likedAt = it },
                    modifier = Modifier
                        .nestedScroll(layoutState.preUpPostDownNestedScrollConnection)
                        .pinchToToggle(
                            key = isShowingLyricsDialog,
                            direction = PinchDirection.Out,
                            threshold = 1.05f,
                            onPinch = {
                                if (isShowingLyrics) isShowingLyricsDialog = true
                            }
                        )
                        .pinchToToggle(
                            key = isShowingLyricsDialog,
                            direction = PinchDirection.In,
                            threshold = .95f,
                            onPinch = {
                                pipHandler.enterPictureInPictureMode()
                            }
                        )
                )
            }
        }

        val controlsContent: @Composable (modifier: Modifier) -> Unit = { innerModifier ->
            Controls(
                media = mediaItem?.toUiMedia(duration),
                binder = binder,
                likedAt = likedAt,
                setLikedAt = { likedAt = it },
                shouldBePlaying = shouldBePlaying,
                position = position,
                modifier = innerModifier
            )
        }

        if (isLandscape) Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = containerModifier.padding(top = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.66f)
                    .padding(bottom = 16.dp)
            ) {
                thumbnailContent(Modifier.padding(horizontal = 16.dp))
            }

            controlsContent(
                Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxHeight()
                    .weight(1f)
            )
        } else Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = containerModifier.padding(top = 4.dp)
        ) {
            // Futuristic Top Command Deck
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Collapse Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF151528).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFFFF9100).copy(alpha = 0.3f), CircleShape)
                        .clickable(onClick = layoutState::collapseSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.chevron_down),
                        contentDescription = "Collapse",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1.15f)
            ) {
                thumbnailContent(Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
            }

            controlsContent(
                Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        if (audioDialogOpen) SliderDialog(
            onDismiss = { audioDialogOpen = false },
            title = stringResource(R.string.playback_settings)
        ) {
            SliderDialogBody(
                provideState = { remember(speed) { mutableFloatStateOf(speed) } },
                onSlideComplete = { speed = it },
                min = 0f,
                max = 2f,
                toDisplay = {
                    if (it <= 0.01f) stringResource(R.string.minimum_speed_value)
                    else stringResource(R.string.format_multiplier, "%.2f".format(it))
                },
                steps = 39,
                label = stringResource(R.string.playback_speed)
            )
            SliderDialogBody(
                provideState = { remember(pitch) { mutableFloatStateOf(pitch) } },
                onSlideComplete = { pitch = it },
                min = 0f,
                max = 2f,
                toDisplay = {
                    if (it <= 0.01f) stringResource(R.string.minimum_speed_value)
                    else stringResource(R.string.format_multiplier, "%.2f".format(it))
                },
                steps = 39,
                label = stringResource(R.string.playback_pitch)
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SecondaryTextButton(
                    text = stringResource(R.string.reset),
                    onClick = {
                        speed = 1f
                        pitch = 1f
                    }
                )
            }
        }

        if (boostDialogOpen) {
            fun submit(state: Float) = transaction {
                mediaItem?.mediaId?.let { mediaId ->
                    Database.setLoudnessBoost(
                        songId = mediaId,
                        loudnessBoost = state.takeUnless { it == 0f }
                    )
                }
            }

            SliderDialog(
                onDismiss = { boostDialogOpen = false },
                title = stringResource(R.string.volume_boost)
            ) {
                SliderDialogBody(
                    provideState = {
                        val state = remember { mutableFloatStateOf(0f) }

                        LaunchedEffect(mediaItem) {
                            mediaItem?.mediaId?.let { mediaId ->
                                Database
                                    .loudnessBoost(mediaId)
                                    .distinctUntilChanged()
                                    .collect { state.floatValue = it ?: 0f }
                            }
                        }

                        state
                    },
                    onSlideComplete = { submit(it) },
                    min = -20f,
                    max = 20f,
                    toDisplay = { stringResource(R.string.format_db, "%.2f".format(it)) }
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SecondaryTextButton(
                        text = stringResource(R.string.reset),
                        onClick = { submit(0f) }
                    )
                }
            }
        }

        if (binder != null) Queue(
            layoutState = playerBottomSheetState,
            binder = binder,
            beforeContent = {
                if (playerLayout == PlayerPreferences.PlayerLayout.New) IconButton(
                    onClick = { trackLoopEnabled = !trackLoopEnabled },
                    icon = R.drawable.infinite,
                    enabled = trackLoopEnabled,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(20.dp)
                ) else Spacer(modifier = Modifier.width(20.dp))
            },
            afterContent = {
                IconButton(
                    icon = R.drawable.ellipsis_horizontal,
                    color = colorPalette.text,
                    onClick = {
                        mediaItem?.let {
                            menuState.display {
                                PlayerMenu(
                                    onDismiss = menuState::hide,
                                    mediaItem = it,
                                    binder = binder,
                                    onShowSpeedDialog = { audioDialogOpen = true },
                                    onShowNormalizationDialog = {
                                        boostDialogOpen = true
                                    }.takeIf { volumeNormalization }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(20.dp)
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            shape = shape
        )
    }
}

@Composable
@OptIn(UnstableApi::class)
private fun PlayerMenu(
    binder: PlayerService.Binder,
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    onShowSpeedDialog: (() -> Unit)? = null,
    onShowNormalizationDialog: (() -> Unit)? = null
) {
    val launchEqualizer by rememberEqualizerLauncher(audioSessionId = { binder.player.audioSessionId })

    BaseMediaItemMenu(
        mediaItem = mediaItem,
        onStartRadio = {
            binder.stopRadio()
            binder.player.seamlessPlay(mediaItem)
            binder.setupRadio(NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId))
        },
        onGoToEqualizer = launchEqualizer,
        onShowSleepTimer = {},
        onDismiss = onDismiss,
        onShowSpeedDialog = onShowSpeedDialog,
        onShowNormalizationDialog = onShowNormalizationDialog
    )
}

private fun onDismiss(binder: PlayerService.Binder) {
    binder.stopRadio()
    binder.player.clearMediaItems()
}
