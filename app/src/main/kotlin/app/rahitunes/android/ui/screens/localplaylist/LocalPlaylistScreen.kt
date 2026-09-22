package app.rahitunes.android.ui.screens.localplaylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import app.rahitunes.android.Database
import app.rahitunes.android.R
import app.rahitunes.android.models.Playlist
import app.rahitunes.android.models.Song
import app.rahitunes.android.ui.components.themed.Scaffold
import app.rahitunes.android.ui.components.themed.adaptiveThumbnailContent
import app.rahitunes.android.ui.screens.GlobalRoutes
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.persist.persist
import app.rahitunes.compose.persist.persistList
import app.rahitunes.compose.routing.RouteHandler
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Route
@Composable
fun LocalPlaylistScreen(playlistId: Long) {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup(prefix = "localPlaylist/$playlistId/")

    RouteHandler {
        GlobalRoutes()

        Content {
            var playlist by persist<Playlist?>("localPlaylist/$playlistId/playlist")
            var songs by persistList<Song>("localPlaylist/$playlistId/songs")

            LaunchedEffect(Unit) {
                Database
                    .playlist(playlistId)
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { playlist = it }
            }

            LaunchedEffect(Unit) {
                Database
                    .playlistSongs(playlistId)
                    .distinctUntilChanged()
                    .collect { songs = it.toImmutableList() }
            }

            val thumbnailContent = remember(playlist) {
                playlist?.thumbnail?.let { url ->
                    adaptiveThumbnailContent(
                        isLoading = false,
                        url = url
                    )
                } ?: { }
            }

            Scaffold(
                key = "localplaylist",
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                tabIndex = 0,
                onTabChange = { },
                tabColumnContent = {
                    tab(0, playlist?.name.orEmpty(), R.drawable.musical_notes)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(currentTabIndex) {
                    playlist?.let {
                        when (currentTabIndex) {
                            0 -> LocalPlaylistSongs(
                                playlist = it,
                                songs = songs,
                                thumbnailContent = thumbnailContent,
                                onDelete = pop
                            )
                        }
                    }
                }
            }
        }
    }
}
