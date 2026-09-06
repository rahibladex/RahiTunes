package app.rahitunes.android.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.Database
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.LocalPlayerServiceBinder
import app.rahitunes.android.R
import app.rahitunes.android.models.Song
import app.rahitunes.android.preferences.DataPreferences
import app.rahitunes.android.query
import app.rahitunes.android.ui.components.LocalMenuState
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.android.ui.components.ShimmerHost
import app.rahitunes.android.ui.components.themed.NonQueuedMediaItemMenu
import app.rahitunes.android.ui.components.themed.TextPlaceholder
import app.rahitunes.android.ui.items.AlbumItem
import app.rahitunes.android.ui.items.AlbumItemPlaceholder
import app.rahitunes.android.ui.items.ArtistItem
import app.rahitunes.android.ui.items.ArtistItemPlaceholder
import app.rahitunes.android.ui.items.PlaylistItem
import app.rahitunes.android.ui.items.PlaylistItemPlaceholder
import app.rahitunes.android.ui.items.SongItem
import app.rahitunes.android.ui.items.SongItemPlaceholder
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
import app.rahitunes.providers.innertube.models.bodies.NextBody
import app.rahitunes.providers.innertube.requests.relatedPage
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class TopStationItem(
    val id: String,
    val rank: String,
    val tag: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val gradientColors: List<Color>,
    val seedVideoId: String,
    val imageUrl: String? = null,
    val iconRes: Int = R.drawable.globe
)

@OptIn(ExperimentalFoundationApi::class)
@Route
@Composable
fun QuickPicks(
    onAlbumClick: (Innertube.AlbumItem) -> Unit,
    onArtistClick: (Innertube.ArtistItem) -> Unit,
    onPlaylistClick: (Innertube.PlaylistItem) -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit = {},
    onSeeAllPlaylistsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMoreMoodsClick: () -> Unit = {},
    onExploreClick: () -> Unit = {}
) {
    val (colorPalette, typography) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current
    val windowInsets = LocalPlayerAwareWindowInsets.current

    var selectedFilter by remember { mutableStateOf("⚡ All Tracks") }
    var relatedPageResult by persist<Result<Innertube.RelatedPage?>?>(tag = "home/relatedPageResult")
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(relatedPageResult, DataPreferences.shouldCacheQuickPicks) {
        if (DataPreferences.shouldCacheQuickPicks)
            relatedPageResult?.getOrNull()?.let { DataPreferences.cachedQuickPicks = it }
        else DataPreferences.cachedQuickPicks = Innertube.RelatedPage()
    }

    // Collect user playback history and favorites for personalized music discovery
    val historySongs by Database.history(10).collectAsState(initial = emptyList())
    val favoriteSongs by Database.favoritesByLikedAtDesc().collectAsState(initial = emptyList())
    val trendingSongs by Database.trending().collectAsState(initial = emptyList())

    // Determine the optimal preference seed based on online listening activity
    val userPreferenceSeed = remember(historySongs, favoriteSongs, trendingSongs) {
        historySongs.firstOrNull { !it.id.startsWith("local:") }?.id
            ?: favoriteSongs.firstOrNull { !it.id.startsWith("local:") }?.id
            ?: trendingSongs.firstOrNull { !it.id.startsWith("local:") }?.id
            ?: "J7p4bzqLvCw"
    }

    val preferredSong = remember(historySongs, favoriteSongs, trendingSongs) {
        historySongs.firstOrNull() ?: favoriteSongs.firstOrNull() ?: trendingSongs.firstOrNull()
    }

    // Dynamic refresh on every screen entry or filter change according to user preference
    LaunchedEffect(selectedFilter, userPreferenceSeed) {
        isRefreshing = true
        val seedVideoId = when {
            selectedFilter.contains("Cyberpunk") -> "4xDzrJKXOOY"
            selectedFilter.contains("Chill") || selectedFilter.contains("Focus") -> "jfKfPfyJRdk"
            selectedFilter.contains("Viral") || selectedFilter.contains("Fresh") -> "kJQP7kiw5Fk"
            else -> userPreferenceSeed
        }

        withContext(Dispatchers.IO) {
            val res = Innertube.relatedPage(body = NextBody(videoId = seedVideoId))
            val finalRes = if ((res == null || res.isFailure) && seedVideoId != "J7p4bzqLvCw") {
                Innertube.relatedPage(body = NextBody(videoId = "J7p4bzqLvCw"))
            } else {
                res
            }
            withContext(Dispatchers.Main) {
                relatedPageResult = finalRes
                isRefreshing = false
            }
        }
    }

    val scrollState = rememberScrollState()
    val quickPicksLazyGridState = rememberLazyGridState()

    val endPaddingValues = windowInsets.only(WindowInsetsSides.End).asPaddingValues()

    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 22.dp, bottom = 10.dp)
        .padding(endPaddingValues)

    val (currentMediaId, playing) = playingSong(binder)

    // Curated Global Top Stations at the top of the feed
    val globalTopStations = remember(userPreferenceSeed, preferredSong, trendingSongs) {
        listOf(
            TopStationItem(
                id = "global_top_50",
                rank = "#1",
                tag = "VIRAL CHARTS",
                title = "Global Top 50 & Viral Hits",
                subtitle = "Worldwide Chart Toppers • 50 Tracks • FLAC",
                accentColor = Color(0xFF00F0FF),
                gradientColors = listOf(Color(0xFF0A192F), Color(0xFF102A43), Color(0xFF0F172A)),
                seedVideoId = trendingSongs.firstOrNull()?.id ?: "kJQP7kiw5Fk",
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300",
                iconRes = R.drawable.trending
            ),
            TopStationItem(
                id = "personal_flow",
                rank = "#2",
                tag = "SMART RADAR",
                title = "Personalized Sonic Flow",
                subtitle = if (preferredSong != null) "Based on ${preferredSong.title} • Infinite Mix" else "AI Tailored to Your Preference • Lossless",
                accentColor = Color(0xFFFF0055),
                gradientColors = listOf(Color(0xFF2A081D), Color(0xFF3B0764), Color(0xFF110E28)),
                seedVideoId = userPreferenceSeed,
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=300",
                iconRes = R.drawable.sparkles
            ),
            TopStationItem(
                id = "cyber_synth",
                rank = "#3",
                tag = "CYBER WAVE",
                title = "Cyber Synth & Electronic",
                subtitle = "Synthwave, Darksynth & Retrowave Hits",
                accentColor = Color(0xFF8B5CF6),
                gradientColors = listOf(Color(0xFF1B0C33), Color(0xFF1E1B4B), Color(0xFF0D1B2A)),
                seedVideoId = "4xDzrJKXOOY",
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300",
                iconRes = R.drawable.globe
            ),
            TopStationItem(
                id = "chill_lofi",
                rank = "#4",
                tag = "DEEP FOCUS",
                title = "Chill Lo-Fi & Deep Ambient",
                subtitle = "Lo-Fi Study Beats & Spatial Relaxation",
                accentColor = Color(0xFF10B981),
                gradientColors = listOf(Color(0xFF06281E), Color(0xFF064E3B), Color(0xFF0F172A)),
                seedVideoId = "jfKfPfyJRdk",
                imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300",
                iconRes = R.drawable.radio
            )
        )
    }

    BoxWithConstraints {
        val quickPicksLazyGridItemWidthFactor =
            if (isLandscape && maxWidth * 0.475f >= 320.dp) 0.475f else 0.85f

        val snapLayoutInfoProvider = rememberSnapLayoutInfo(
            lazyGridState = quickPicksLazyGridState,
            positionInLayout = { layoutSize, itemSize ->
                (layoutSize * quickPicksLazyGridItemWidthFactor / 2f - itemSize / 2f)
            }
        )

        val itemInHorizontalGridWidth = maxWidth * quickPicksLazyGridItemWidthFactor

        Column(
            modifier = Modifier
                .background(Color(0xFF07080D))
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    windowInsets
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                )
                .padding(bottom = Dimensions.bottomBar.height + Dimensions.bottomBar.bottomMargin + 32.dp)
        ) {
            // HomeTopBar with Interactive Search Capsule & Category Filters
            HomeTopBar(
                userName = "Alex",
                selectedFilter = selectedFilter,
                onFilterSelected = { filter ->
                    selectedFilter = filter
                    when {
                        filter.contains("Chill") || filter.contains("Focus") -> onMoreMoodsClick()
                        filter.contains("Drops") -> onSeeAllPlaylistsClick()
                        filter.contains("Viral") || filter.contains("Cyberpunk") -> onExploreClick()
                    }
                },
                onAvatarClick = onSettingsClick,
                onSearchClick = onSearchClick,
                onFavoritesClick = onFavoritesClick
            )

            // SECTION 1: GLOBAL TOP STATIONS (Top of the feed right below TopBar)
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
                                    listOf(Color(0xFFFF0055), Color(0xFF00F0FF))
                                )
                            )
                    )
                    BasicText(
                        text = "Global Top Stations",
                        style = typography.m.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.5.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF0055).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFF0055).copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        BasicText(
                            text = "LIVE RADAR",
                            style = typography.xxs.copy(
                                color = Color(0xFFFF0055),
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                BasicText(
                    text = "See all",
                    style = typography.xs.copy(
                        color = Color(0xFF00F0FF),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onSeeAllPlaylistsClick() }
                )
            }

            // Top Station Cards Stack
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                globalTopStations.forEach { station ->
                    val isCurrentStation = currentMediaId == station.seedVideoId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = station.accentColor.copy(alpha = 0.35f)
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF0F121C),
                                        Color(0xFF141824)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                if (isCurrentStation) station.accentColor else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                binder?.stopRadio()
                                binder?.setupRadio(
                                    NavigationEndpoint.Endpoint.Watch(videoId = station.seedVideoId)
                                )
                                binder?.player?.play()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Rank Number Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(station.accentColor.copy(alpha = 0.18f))
                                    .border(
                                        1.2.dp,
                                        station.accentColor.copy(alpha = 0.6f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicText(
                                    text = station.rank,
                                    style = typography.xs.copy(
                                        color = station.accentColor,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            // Square Artwork with Glow
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF181E2E)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!station.imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = station.imageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(station.iconRes),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(station.accentColor),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Title and Subtitle Info
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BasicText(
                                        text = station.title,
                                        style = typography.xs.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(station.accentColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        BasicText(
                                            text = station.tag,
                                            style = typography.xxs.copy(
                                                color = station.accentColor,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 8.5.sp
                                            )
                                        )
                                    }

                                    BasicText(
                                        text = station.subtitle,
                                        style = typography.xxs.copy(
                                            color = Color(0xFF8E9AA8),
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Right-aligned Dynamic Play / Radio Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = CircleShape,
                                    spotColor = station.accentColor.copy(alpha = 0.4f)
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(station.accentColor.copy(alpha = 0.25f), Color(0xFF181E2E))
                                    )
                                )
                                .border(1.2.dp, station.accentColor.copy(alpha = 0.5f), CircleShape)
                                .clickable {
                                    binder?.stopRadio()
                                    binder?.setupRadio(
                                        NavigationEndpoint.Endpoint.Watch(videoId = station.seedVideoId)
                                    )
                                    binder?.player?.play()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrentStation && playing) {
                                MusicBars(
                                    color = station.accentColor,
                                    modifier = Modifier.size(14.dp, 12.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.play),
                                    contentDescription = "Play Station",
                                    colorFilter = ColorFilter.tint(station.accentColor),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // SECTION 2: QUICK PICKS SONGS & TAILORED FLOW
            relatedPageResult?.getOrNull()?.let { related ->
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
                                .background(Color(0xFF00F0FF))
                        )
                        BasicText(
                            text = stringResource(R.string.quick_picks),
                            style = typography.m.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.5.sp
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        MusicBars(
                            color = Color(0xFF00F0FF),
                            modifier = Modifier.size(12.dp, 9.dp)
                        )
                        BasicText(
                            text = if (isRefreshing) "REFRESHING..." else "PREFERENCE FLOW",
                            style = typography.xxs.copy(
                                color = Color(0xFF00F0FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                LazyHorizontalGrid(
                    state = quickPicksLazyGridState,
                    rows = GridCells.Fixed(4),
                    flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider),
                    contentPadding = endPaddingValues,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((Dimensions.thumbnails.song + 20.dp) * 4)
                        .padding(horizontal = 12.dp)
                ) {
                    preferredSong?.let { song ->
                        item {
                            SongItem(
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = {
                                            menuState.display {
                                                NonQueuedMediaItemMenu(
                                                    onDismiss = menuState::hide,
                                                    mediaItem = song.asMediaItem,
                                                    onRemoveFromQuickPicks = {
                                                        query {
                                                            Database.clearEventsFor(song.id)
                                                        }
                                                    }
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
                                    .width(itemInHorizontalGridWidth),
                                song = song,
                                thumbnailSize = Dimensions.thumbnails.song,
                                trailingContent = {
                                    Image(
                                        painter = painterResource(R.drawable.star),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color(0xFF00F0FF)),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                showDuration = false,
                                isPlaying = playing && currentMediaId == song.id
                            )
                        }
                    }

                    items(
                        items = related.songs?.dropLast(if (preferredSong == null) 0 else 1)
                            ?: emptyList(),
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
                                .width(itemInHorizontalGridWidth),
                            showDuration = false,
                            isPlaying = playing && currentMediaId == song.key
                        )
                    }
                }

                related.albums?.let { albums ->
                    Row(
                        modifier = sectionTextModifier.fillMaxWidth(),
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
                                    .background(Color(0xFF8B5CF6))
                            )
                            BasicText(
                                text = stringResource(R.string.related_albums),
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = endPaddingValues,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        items(
                            items = albums,
                            key = Innertube.AlbumItem::key
                        ) { album ->
                            AlbumItem(
                                album = album,
                                thumbnailSize = Dimensions.thumbnails.album,
                                alternative = true,
                                modifier = Modifier.clickable { onAlbumClick(album) }
                            )
                        }
                    }
                }

                related.artists?.let { artists ->
                    Row(
                        modifier = sectionTextModifier.fillMaxWidth(),
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
                                    .background(Color(0xFFFF0055))
                            )
                            BasicText(
                                text = stringResource(R.string.similar_artists),
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = endPaddingValues,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        items(
                            items = artists,
                            key = Innertube.ArtistItem::key
                        ) { artist ->
                            ArtistItem(
                                artist = artist,
                                thumbnailSize = Dimensions.thumbnails.artist,
                                alternative = true,
                                modifier = Modifier.clickable { onArtistClick(artist) }
                            )
                        }
                    }
                }

                related.playlists?.let { playlists ->
                    Row(
                        modifier = sectionTextModifier.fillMaxWidth(),
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
                                    .background(Color(0xFFF59E0B))
                            )
                            BasicText(
                                text = stringResource(R.string.recommended_playlists),
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = endPaddingValues,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        items(
                            items = playlists,
                            key = Innertube.PlaylistItem::key
                        ) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                thumbnailSize = Dimensions.thumbnails.playlist,
                                alternative = true,
                                modifier = Modifier.clickable { onPlaylistClick(playlist) }
                            )
                        }
                    }
                }

                Unit
            } ?: if (isRefreshing) {
                ShimmerHost {
                    repeat(4) {
                        SongItemPlaceholder(thumbnailSize = Dimensions.thumbnails.song)
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
            } else if (historySongs.isNotEmpty() || trendingSongs.isNotEmpty()) {
                val fallbackSongs = (historySongs + trendingSongs).distinctBy { it.id }.take(8)
                Column {
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
                                    .background(Color(0xFF00F0FF))
                            )
                            BasicText(
                                text = "Your Top Picks",
                                style = typography.m.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.5.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            MusicBars(
                                color = Color(0xFF00F0FF),
                                modifier = Modifier.size(12.dp, 9.dp)
                            )
                            BasicText(
                                text = "OFFLINE FLOW",
                                style = typography.xxs.copy(
                                    color = Color(0xFF00F0FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    LazyHorizontalGrid(
                        state = quickPicksLazyGridState,
                        rows = GridCells.Fixed(minOf(fallbackSongs.size, 4)),
                        flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider),
                        contentPadding = endPaddingValues,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((Dimensions.thumbnails.song + 20.dp) * minOf(fallbackSongs.size, 4))
                            .padding(horizontal = 12.dp)
                    ) {
                        items(
                            items = fallbackSongs,
                            key = Song::id
                        ) { song ->
                            SongItem(
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = {
                                            menuState.display {
                                                NonQueuedMediaItemMenu(
                                                    onDismiss = menuState::hide,
                                                    mediaItem = song.asMediaItem,
                                                    onRemoveFromQuickPicks = {
                                                        query {
                                                            Database.clearEventsFor(song.id)
                                                        }
                                                    }
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
                                    .width(itemInHorizontalGridWidth),
                                song = song,
                                thumbnailSize = Dimensions.thumbnails.song,
                                trailingContent = {
                                    Image(
                                        painter = painterResource(R.drawable.star),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color(0xFF00F0FF)),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                showDuration = false,
                                isPlaying = playing && currentMediaId == song.id
                            )
                        }
                    }
                }
            } else {
                ShimmerHost {
                    repeat(4) {
                        SongItemPlaceholder(thumbnailSize = Dimensions.thumbnails.song)
                    }
                }
            }
        }
    }
}
