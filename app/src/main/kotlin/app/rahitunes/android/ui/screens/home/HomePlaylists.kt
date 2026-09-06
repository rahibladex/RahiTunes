package app.rahitunes.android.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.Database
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.R
import app.rahitunes.android.models.PipedSession
import app.rahitunes.android.models.Playlist
import app.rahitunes.android.models.PlaylistPreview
import app.rahitunes.android.preferences.DataPreferences
import app.rahitunes.android.preferences.OrderPreferences
import app.rahitunes.android.preferences.UIStatePreferences
import app.rahitunes.android.query
import app.rahitunes.android.ui.components.themed.HeaderIconButton
import app.rahitunes.android.ui.components.themed.TextFieldDialog
import app.rahitunes.android.ui.items.PlaylistItem
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.android.ui.screens.builtinplaylist.BuiltInPlaylistScreen
import app.rahitunes.android.ui.screens.settings.SettingsEntryGroupText
import app.rahitunes.android.ui.screens.settings.SettingsGroupSpacer
import app.rahitunes.compose.persist.persist
import app.rahitunes.compose.persist.persistList
import app.rahitunes.core.data.enums.BuiltInPlaylist
import app.rahitunes.core.data.enums.PlaylistSortBy
import app.rahitunes.core.data.enums.SortOrder
import app.rahitunes.core.ui.Dimensions
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.providers.piped.Piped
import app.rahitunes.providers.piped.models.Session
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import app.rahitunes.providers.piped.models.PlaylistPreview as PipedPlaylistPreview

@Route
@Composable
fun HomePlaylists(
    onBuiltInPlaylist: (BuiltInPlaylist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onPipedPlaylistClick: (Session, PipedPlaylistPreview) -> Unit,
    onSearchClick: () -> Unit
) = with(OrderPreferences) {
    val (colorPalette) = LocalAppearance.current

    var isCreatingANewPlaylist by rememberSaveable { mutableStateOf(false) }

    if (isCreatingANewPlaylist) TextFieldDialog(
        hintText = stringResource(R.string.enter_playlist_name_prompt),
        onDismiss = { isCreatingANewPlaylist = false },
        onAccept = { text ->
            query {
                Database.insert(Playlist(name = text))
            }
        }
    )
    var items by persistList<PlaylistPreview>("home/playlists")
    var pipedSessions by persist<Map<PipedSession, List<PipedPlaylistPreview>?>>("home/piped")

    LaunchedEffect(playlistSortBy, playlistSortOrder) {
        Database
            .playlistPreviews(playlistSortBy, playlistSortOrder)
            .collect { items = it.toImmutableList() }
    }

    LaunchedEffect(Unit) {
        Database.pipedSessions().collect { sessions ->
            pipedSessions = sessions.associateWith { session ->
                async {
                    Piped.playlist.list(session = session.toApiSession())?.getOrNull()
                }
            }.mapValues { (_, value) -> value.await() }
        }
    }

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (playlistSortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = ""
    )

    val lazyGridState = rememberLazyGridState()
    val builtInPlaylists by BuiltInPlaylistScreen.shownPlaylistsAsState()

    val cardBg = Color(0xFF0F121C)
    val cardBorder = Color.White.copy(alpha = 0.06f)
    val neonCyan = Color(0xFF00F0FF)
    val neonPink = Color(0xFFFF0055)
    val amber = Color(0xFFF59E0B)
    val slateMuted = Color(0xFF8E9AA8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080D))
    ) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = if (UIStatePreferences.playlistsAsGrid)
                GridCells.Adaptive(150.dp)
            else GridCells.Fixed(1),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 10.dp,
                bottom = 120.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Dashboard
            item(key = "playlists_header", contentType = 0, span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    // Full-width interactive Search Capsule
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color(0xFF00F0FF).copy(alpha = 0.12f)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(cardBg)
                            .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .clickable(onClick = onSearchClick)
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(neonCyan, neonPink))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = null,
                                        tint = Color(0xFF07080D),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Search playlists & collections",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "CURATED DECKS • AUTO-SYNC",
                                        color = slateMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title Row with Action Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PLAYLIST VAULT",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${items.size} custom decks active",
                                color = slateMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Create New Playlist Gradient Pill
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = neonCyan.copy(alpha = 0.4f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(neonCyan, Color(0xFF0077FF))))
                                .clickable { isCreatingANewPlaylist = true }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "+ NEW DECK",
                                color = Color(0xFF07080D),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Grid / List View Toggle
                        HeaderIconButton(
                            icon = if (UIStatePreferences.playlistsAsGrid) R.drawable.grid else R.drawable.list,
                            onClick = {
                                UIStatePreferences.playlistsAsGrid = !UIStatePreferences.playlistsAsGrid
                            }
                        )

                        // Sort Order Toggle
                        HeaderIconButton(
                            icon = R.drawable.arrow_up,
                            color = neonCyan,
                            onClick = { playlistSortOrder = !playlistSortOrder },
                            modifier = Modifier.graphicsLayer { rotationZ = sortOrderIconRotation }
                        )
                    }
                }
            }

            // Smart Quick Playlists
            if (BuiltInPlaylist.Favorites in builtInPlaylists) item(key = "favorites") {
                SmartPlaylistCard(
                    title = stringResource(R.string.favorites),
                    subtitle = "Hearted Soundtracks",
                    icon = R.drawable.heart,
                    accentColor = neonPink,
                    onClick = { onBuiltInPlaylist(BuiltInPlaylist.Favorites) }
                )
            }

            if (BuiltInPlaylist.Offline in builtInPlaylists) item(key = "offline") {
                SmartPlaylistCard(
                    title = stringResource(R.string.offline),
                    subtitle = "Downloaded Vault",
                    icon = R.drawable.airplane,
                    accentColor = neonCyan,
                    onClick = { onBuiltInPlaylist(BuiltInPlaylist.Offline) }
                )
            }

            if (BuiltInPlaylist.Top in builtInPlaylists) item(key = "top") {
                SmartPlaylistCard(
                    title = "Top Velocity",
                    subtitle = "Most Played Tracks",
                    icon = R.drawable.trending,
                    accentColor = amber,
                    onClick = { onBuiltInPlaylist(BuiltInPlaylist.Top) }
                )
            }

            if (BuiltInPlaylist.History in builtInPlaylists) item(key = "history") {
                SmartPlaylistCard(
                    title = stringResource(R.string.history),
                    subtitle = "Recent Playback Log",
                    icon = R.drawable.history,
                    accentColor = slateMuted,
                    onClick = { onBuiltInPlaylist(BuiltInPlaylist.History) }
                )
            }

            // Custom User Playlists
            items(
                items = items,
                key = { it.playlist.id }
            ) { playlistPreview ->
                PlaylistItem(
                    playlist = playlistPreview,
                    thumbnailSize = Dimensions.thumbnails.playlist,
                    alternative = UIStatePreferences.playlistsAsGrid,
                    modifier = Modifier
                        .clickable(onClick = { onPlaylistClick(playlistPreview.playlist) })
                        .animateItem(fadeInSpec = null, fadeOutSpec = null)
                )
            }

            // Piped Synced Playlists
            pipedSessions
                ?.ifEmpty { null }
                ?.filter { it.value?.isNotEmpty() == true }
                ?.forEach { (session, playlists) ->
                    item(
                        key = "piped-header-${session.username}",
                        contentType = 0,
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        SettingsGroupSpacer()
                        SettingsEntryGroupText(title = session.username)
                    }

                    playlists?.let {
                        items(
                            items = playlists,
                            key = { "piped-${session.username}-${it.id}" }
                        ) { playlist ->
                            PlaylistItem(
                                name = playlist.name,
                                songCount = playlist.videoCount,
                                channelName = null,
                                thumbnailUrl = playlist.thumbnailUrl.toString(),
                                thumbnailSize = Dimensions.thumbnails.playlist,
                                alternative = UIStatePreferences.playlistsAsGrid,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        onPipedPlaylistClick(
                                            session.toApiSession(),
                                            playlist
                                        )
                                    })
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun SmartPlaylistCard(
    title: String,
    subtitle: String,
    icon: Int,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = accentColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F121C))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF8E9AA8),
                    fontSize = 10.5.sp
                )
            }

            Icon(
                painter = painterResource(R.drawable.chevron_forward),
                contentDescription = null,
                tint = Color(0xFF8E9AA8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
