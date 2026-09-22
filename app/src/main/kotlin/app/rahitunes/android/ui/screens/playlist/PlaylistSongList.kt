package app.rahitunes.android.ui.screens.playlist

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.Database
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.LocalPlayerServiceBinder
import app.rahitunes.android.R
import app.rahitunes.android.models.Playlist
import app.rahitunes.android.models.SongPlaylistMap
import app.rahitunes.android.query
import app.rahitunes.android.transaction
import app.rahitunes.android.ui.components.LocalMenuState
import app.rahitunes.android.ui.components.ShimmerHost
import app.rahitunes.android.ui.components.themed.FloatingActionsContainerWithScrollToTop
import app.rahitunes.android.ui.components.themed.Header
import app.rahitunes.android.ui.components.themed.HeaderIconButton
import app.rahitunes.android.ui.components.themed.HeaderPlaceholder
import app.rahitunes.android.ui.components.themed.LayoutWithAdaptiveThumbnail
import app.rahitunes.android.ui.components.themed.NonQueuedMediaItemMenu
import app.rahitunes.android.ui.components.themed.PlaylistInfo
import app.rahitunes.android.ui.components.themed.SecondaryTextButton
import app.rahitunes.android.ui.components.themed.TextFieldDialog
import app.rahitunes.android.ui.components.themed.adaptiveThumbnailContent
import app.rahitunes.android.ui.items.SongItem
import app.rahitunes.android.ui.items.SongItemPlaceholder
import app.rahitunes.android.utils.PlaylistDownloadIcon
import app.rahitunes.android.utils.asMediaItem
import app.rahitunes.android.utils.completed
import app.rahitunes.android.utils.enqueue
import app.rahitunes.android.utils.forcePlayAtIndex
import app.rahitunes.android.utils.forcePlayFromBeginning
import app.rahitunes.android.utils.playingSong
import app.rahitunes.compose.persist.persist
import app.rahitunes.core.ui.Dimensions
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.utils.isLandscape
import app.rahitunes.providers.innertube.Innertube
import app.rahitunes.providers.innertube.models.bodies.BrowseBody
import app.rahitunes.providers.innertube.requests.playlistPage
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.valentinilk.shimmer.shimmer
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistSongList(
    browseId: String,
    params: String?,
    maxDepth: Int?,
    shouldDedup: Boolean,
    onTitleLoaded: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current
    val context = LocalContext.current
    val menuState = LocalMenuState.current

    var playlistPage by persist<Innertube.PlaylistOrAlbumPage?>("playlist/$browseId/playlistPage")

    LaunchedEffect(Unit) {
        if (playlistPage != null) {
            playlistPage?.title?.let(onTitleLoaded)
            if (playlistPage?.songsPage?.continuation == null) return@LaunchedEffect
        }

        playlistPage = withContext(Dispatchers.IO) {
            Innertube
                .playlistPage(BrowseBody(browseId = browseId, params = params))
                ?.completed(
                    maxDepth = maxDepth ?: Int.MAX_VALUE,
                    shouldDedup = shouldDedup
                )
                ?.getOrNull()
        }
        playlistPage?.title?.let(onTitleLoaded)
    }

    var isImportingPlaylist by rememberSaveable { mutableStateOf(false) }

    if (isImportingPlaylist) TextFieldDialog(
        hintText = stringResource(R.string.enter_playlist_name_prompt),
        initialTextInput = playlistPage?.title.orEmpty(),
        onDismiss = { isImportingPlaylist = false },
        onAccept = { text ->
            query {
                transaction {
                    val playlistId = Database.insert(
                        Playlist(
                            name = text,
                            browseId = browseId,
                            thumbnail = playlistPage?.thumbnail?.url
                        )
                    )

                    playlistPage?.songsPage?.items
                        ?.map(Innertube.SongItem::asMediaItem)
                        ?.onEach(Database::insert)
                        ?.mapIndexed { index, mediaItem ->
                            SongPlaylistMap(
                                songId = mediaItem.mediaId,
                                playlistId = playlistId,
                                position = index
                            )
                        }?.let(Database::insertSongPlaylistMaps)
                }
            }
        }
    )

    val headerContent: @Composable () -> Unit = {
        if (playlistPage == null) HeaderPlaceholder(modifier = Modifier.shimmer())
        else Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                text = playlistPage?.title.orEmpty(),
                style = typography.l.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Enqueue Action Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (playlistPage?.songsPage?.items?.isNotEmpty() == true) {
                                Brush.horizontalGradient(listOf(Color(0xFFFF9100), Color(0xFFFF1212)))
                            } else {
                                SolidColor(Color(0xFF1F2637))
                            }
                        )
                        .clickable(
                            enabled = playlistPage?.songsPage?.items?.isNotEmpty() == true,
                            onClick = {
                                playlistPage?.songsPage?.items?.map(Innertube.SongItem::asMediaItem)
                                    ?.let { mediaItems ->
                                        binder?.player?.enqueue(mediaItems)
                                    }
                            }
                        )
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    BasicText(
                        text = stringResource(R.string.enqueue),
                        style = typography.xs.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Download, Add, Share Action Icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    playlistPage?.songsPage?.items?.map(Innertube.SongItem::asMediaItem)
                        ?.let { PlaylistDownloadIcon(songs = it.toImmutableList()) }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF141724))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .clickable { isImportingPlaylist = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "Add",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF141724))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .clickable {
                                val url = playlistPage?.url
                                    ?: "https://music.youtube.com/playlist?list=${browseId.removePrefix("VL")}"

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, url)
                                }

                                context.startActivity(Intent.createChooser(sendIntent, null))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.share_social),
                            contentDescription = "Share",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    val thumbnailContent = adaptiveThumbnailContent(
        isLoading = playlistPage == null,
        url = playlistPage?.thumbnail?.url
    )

    val lazyListState = rememberLazyListState()

    val (currentMediaId, playing) = playingSong(binder)

    LayoutWithAdaptiveThumbnail(
        thumbnailContent = thumbnailContent,
        modifier = modifier
    ) {
        Box {
            LazyColumn(
                state = lazyListState,
                contentPadding = LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
                    .asPaddingValues(),
                modifier = Modifier
                    .background(colorPalette.background0)
                    .fillMaxSize()
            ) {
                item(
                    key = "header",
                    contentType = 0
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        headerContent()
                        if (!isLandscape) thumbnailContent()
                        PlaylistInfo(playlist = playlistPage)
                    }
                }

                itemsIndexed(items = playlistPage?.songsPage?.items ?: emptyList()) { index, song ->
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
                                    playlistPage?.songsPage?.items?.map(Innertube.SongItem::asMediaItem)
                                        ?.let { mediaItems ->
                                            binder?.stopRadio()
                                            binder?.player?.forcePlayAtIndex(mediaItems, index)
                                        }
                                }
                            ),
                        isPlaying = playing && currentMediaId == song.key
                    )
                }

                if (playlistPage == null) item(key = "loading") {
                    ShimmerHost(modifier = Modifier.fillParentMaxSize()) {
                        repeat(4) {
                            SongItemPlaceholder(thumbnailSize = Dimensions.thumbnails.song)
                        }
                    }
                }
            }

            FloatingActionsContainerWithScrollToTop(
                lazyListState = lazyListState,
                icon = R.drawable.shuffle,
                onClick = {
                    playlistPage?.songsPage?.items?.let { songs ->
                        if (songs.isNotEmpty()) {
                            binder?.stopRadio()
                            binder?.player?.forcePlayFromBeginning(
                                songs.shuffled().map(Innertube.SongItem::asMediaItem)
                            )
                        }
                    }
                }
            )
        }
    }
}
