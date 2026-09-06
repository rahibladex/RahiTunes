package app.rahitunes.android.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.LocalPlayerServiceBinder
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.LocalMenuState
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.android.ui.components.ShimmerHost
import app.rahitunes.android.ui.components.themed.FloatingActionsContainerWithScrollToTop
import app.rahitunes.android.ui.components.themed.NonQueuedMediaItemMenu
import app.rahitunes.android.ui.components.themed.TextPlaceholder
import app.rahitunes.android.ui.items.AlbumItem
import app.rahitunes.android.ui.items.AlbumItemPlaceholder
import app.rahitunes.android.ui.items.SongItem
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.android.utils.asMediaItem
import app.rahitunes.android.utils.center
import app.rahitunes.android.utils.forcePlay
import app.rahitunes.android.utils.playingSong
import app.rahitunes.android.utils.rememberSnapLayoutInfo
import app.rahitunes.android.utils.secondary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.compose.persist.persist
import app.rahitunes.core.ui.Dimensions
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.utils.isLandscape
import app.rahitunes.providers.innertube.Innertube
import app.rahitunes.providers.innertube.models.NavigationEndpoint
import app.rahitunes.providers.innertube.requests.discoverPage

@OptIn(ExperimentalFoundationApi::class)
@Route
@Composable
fun HomeDiscovery(
    onMoodClick: (mood: Innertube.Mood.Item) -> Unit,
    onNewReleaseAlbumClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onMoreMoodsClick: () -> Unit,
    onMoreAlbumsClick: () -> Unit,
    onPlaylistClick: (browseId: String) -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    val windowInsets = LocalPlayerAwareWindowInsets.current
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current

    val scrollState = rememberScrollState()
    val moodGridState = rememberLazyGridState()

    val endPaddingValues = windowInsets
        .only(WindowInsetsSides.End)
        .asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 22.dp, bottom = 10.dp)
        .padding(endPaddingValues)

    var discoverPage by persist<Result<Innertube.DiscoverPage>?>("home/discovery")

    LaunchedEffect(Unit) {
        if (discoverPage?.isSuccess != true) discoverPage = Innertube.discoverPage()
    }

    BoxWithConstraints {
        val widthFactor = if (isLandscape && maxWidth * 0.475f >= 320.dp) 0.475f else 0.85f
        val moodSnapLayoutInfoProvider = rememberSnapLayoutInfo(
            lazyGridState = moodGridState,
            positionInLayout = { layoutSize, itemSize ->
                layoutSize * widthFactor / 2f - itemSize / 2f
            }
        )
        val itemWidth = maxWidth * widthFactor

        Column(
            modifier = Modifier
                .background(Color(0xFF07080D))
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    windowInsets
                        .only(WindowInsetsSides.Vertical)
                        .asPaddingValues()
                )
                .padding(bottom = Dimensions.bottomBar.height + Dimensions.bottomBar.bottomMargin + 32.dp)
        ) {
            // Futuristic Header Title & Discover Radar Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00F0FF))
                            )
                            BasicText(
                                text = "GLOBAL RADAR",
                                style = typography.xxs.copy(
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp
                                )
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        BasicText(
                            text = "Sound Exploration",
                            style = typography.l.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        )
                    }

                    // Pulse Radar Icon Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF00F0FF).copy(alpha = 0.4f)
                            )
                            .clip(CircleShape)
                            .background(Color(0xFF0F121C))
                            .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f), CircleShape)
                            .clickable(onClick = onSearchClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.globe),
                            contentDescription = "Radar",
                            colorFilter = ColorFilter.tint(Color(0xFF00F0FF)),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Full-width Search Capsule
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0xFF00F0FF).copy(alpha = 0.15f)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0F121C), Color(0xFF181E2E))
                            )
                        )
                        .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .clickable(onClick = onSearchClick)
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "Search",
                            colorFilter = ColorFilter.tint(Color(0xFF00F0FF)),
                            modifier = Modifier.size(18.dp)
                        )
                        BasicText(
                            text = "Explore genres, trending vibes & moods...",
                            style = typography.xs.copy(
                                color = Color(0xFF8E9AA8),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            discoverPage?.getOrNull()?.let { page ->
                // Section: Moods & Genres Explorer
                if (page.moods.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp, 16.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF00F0FF))
                            )
                            BasicText(
                                text = "Moods & Vibes Matrix",
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            )
                        }

                        BasicText(
                            text = "View all",
                            style = typography.xs.copy(
                                color = Color(0xFF00F0FF),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable { onMoreMoodsClick() }
                        )
                    }

                    LazyHorizontalGrid(
                        state = moodGridState,
                        rows = GridCells.Fixed(4),
                        flingBehavior = rememberSnapFlingBehavior(moodSnapLayoutInfoProvider),
                        contentPadding = endPaddingValues,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((4 * (52 + 8)).dp)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        items(
                            items = page.moods.sortedBy { it.title },
                            key = { it.endpoint.params ?: it.title }
                        ) {
                            MoodItem(
                                mood = it,
                                onClick = { it.endpoint.browseId?.let { _ -> onMoodClick(it) } },
                                modifier = Modifier
                                    .width(itemWidth)
                            )
                        }
                    }
                }

                // Section: Fresh New Releases
                if (page.newReleaseAlbums.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp, 16.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF8B5CF6))
                            )
                            BasicText(
                                text = "Fresh Release Radar",
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            )
                        }

                        BasicText(
                            text = "More",
                            style = typography.xs.copy(
                                color = Color(0xFF00F0FF),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable { onMoreAlbumsClick() }
                        )
                    }

                    LazyRow(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = page.newReleaseAlbums, key = { it.key }) {
                            AlbumItem(
                                album = it,
                                thumbnailSize = Dimensions.thumbnails.album,
                                alternative = true,
                                modifier = Modifier.clickable(onClick = { onNewReleaseAlbumClick(it.key) })
                            )
                        }
                    }
                }

                // Section: Trending Velocity Tracks
                if (page.trending.songs.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp, 16.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFFF0055))
                            )
                            BasicText(
                                text = "Trending Global Velocity",
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            )
                        }

                        page.trending.endpoint?.browseId?.let { browseId ->
                            BasicText(
                                text = "Playlist",
                                style = typography.xs.copy(
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.clickable { onPlaylistClick(browseId) }
                            )
                        }
                    }

                    val trendingGridState = rememberLazyGridState()
                    val trendingSnapLayoutInfoProvider = rememberSnapLayoutInfo(
                        lazyGridState = trendingGridState,
                        positionInLayout = { layoutSize, itemSize ->
                            (layoutSize * widthFactor / 2f - itemSize / 2f)
                        }
                    )

                    val (currentMediaId, playing) = playingSong(binder)

                    LazyHorizontalGrid(
                        state = trendingGridState,
                        rows = GridCells.Fixed(4),
                        flingBehavior = rememberSnapFlingBehavior(trendingSnapLayoutInfoProvider),
                        contentPadding = endPaddingValues,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((Dimensions.thumbnails.song + 20.dp) * 4)
                            .padding(horizontal = 12.dp)
                    ) {
                        items(
                            items = page.trending.songs,
                            key = Innertube.SongItem::key
                        ) { song ->
                            SongItem(
                                song = song,
                                thumbnailSize = Dimensions.thumbnails.song,
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = {
                                            menuState.display {
                                                NonQueuedMediaItemMenu(
                                                    onDismiss = menuState::hide,
                                                    mediaItem = song.asMediaItem
                                                )
                                            }
                                        },
                                        onClick = {
                                            val mediaItem = song.asMediaItem
                                            binder?.stopRadio()
                                            binder?.player?.forcePlay(mediaItem)
                                            binder?.setupRadio(
                                                NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId)
                                            )
                                        }
                                    )
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .width(itemWidth),
                                showDuration = false,
                                isPlaying = playing && currentMediaId == song.key
                            )
                        }
                    }
                }
            } ?: discoverPage?.exceptionOrNull()?.let {
                BasicText(
                    text = stringResource(R.string.error_message),
                    style = typography.s.secondary.center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 16.dp)
                )
            } ?: ShimmerHost {
                TextPlaceholder(modifier = sectionTextModifier)
                LazyHorizontalGrid(
                    state = moodGridState,
                    rows = GridCells.Fixed(4),
                    flingBehavior = rememberSnapFlingBehavior(moodSnapLayoutInfoProvider),
                    contentPadding = endPaddingValues,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((Dimensions.items.moodHeight + 4.dp) * 4)
                ) {
                    items(16) {
                        MoodItemPlaceholder(
                            width = itemWidth,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                TextPlaceholder(modifier = sectionTextModifier)
                Row {
                    repeat(2) {
                        AlbumItemPlaceholder(
                            thumbnailSize = Dimensions.thumbnails.album,
                            alternative = true
                        )
                    }
                }
            }
        }

        FloatingActionsContainerWithScrollToTop(
            scrollState = scrollState,
            icon = R.drawable.search,
            onClick = onSearchClick
        )
    }
}

@Composable
fun MoodItem(
    mood: Innertube.Mood.Item,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = LocalAppearance.current.typography

    val baseColor by remember { derivedStateOf { Color(mood.stripeColor) } }

    Box(
        modifier = modifier
            .height(50.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = baseColor.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        baseColor.copy(alpha = 0.85f),
                        baseColor.copy(alpha = 0.45f),
                        Color(0xFF0F121C)
                    )
                )
            )
            .border(1.2.dp, baseColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicText(
            text = mood.title,
            style = typography.xs.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
fun MoodItemPlaceholder(
    width: Dp,
    modifier: Modifier = Modifier
) = Spacer(
    modifier = modifier
        .background(
            color = Color(0xFF0F121C),
            shape = RoundedCornerShape(16.dp)
        )
        .size(
            width = width,
            height = 50.dp
        )
)
