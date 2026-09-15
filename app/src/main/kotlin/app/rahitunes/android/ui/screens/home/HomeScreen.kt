package app.rahitunes.android.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import app.rahitunes.android.R
import app.rahitunes.android.models.toUiMood
import app.rahitunes.android.preferences.UIStatePreferences
import app.rahitunes.android.ui.components.themed.Scaffold
import app.rahitunes.android.ui.screens.GlobalRoutes
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.android.ui.screens.albumRoute
import app.rahitunes.android.ui.screens.artistRoute
import app.rahitunes.android.ui.screens.builtInPlaylistRoute
import app.rahitunes.android.ui.screens.builtinplaylist.BuiltInPlaylistScreen
import app.rahitunes.android.ui.screens.localPlaylistRoute
import app.rahitunes.android.ui.screens.localplaylist.LocalPlaylistScreen
import app.rahitunes.android.ui.screens.mood.MoodScreen
import app.rahitunes.android.ui.screens.mood.MoreAlbumsScreen
import app.rahitunes.android.ui.screens.mood.MoreMoodsScreen
import app.rahitunes.android.ui.screens.moodRoute
import app.rahitunes.android.ui.screens.pipedPlaylistRoute
import app.rahitunes.android.ui.screens.playlistRoute
import app.rahitunes.android.ui.screens.searchResultRoute
import app.rahitunes.android.ui.screens.searchRoute
import app.rahitunes.android.ui.screens.settings.SettingsScreen
import app.rahitunes.android.ui.screens.settingsRoute
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.routing.Route0
import app.rahitunes.compose.routing.RouteHandler
import app.rahitunes.core.data.enums.BuiltInPlaylist

private val moreMoodsRoute = Route0("moreMoodsRoute")
private val moreAlbumsRoute = Route0("moreAlbumsRoute")

@Route
@Composable
fun HomeScreen() {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup("home/")

    RouteHandler {
        GlobalRoutes()

        localPlaylistRoute { playlistId ->
            LocalPlaylistScreen(playlistId = playlistId)
        }

        builtInPlaylistRoute { builtInPlaylist ->
            BuiltInPlaylistScreen(builtInPlaylist = builtInPlaylist)
        }

        moodRoute { mood ->
            MoodScreen(mood = mood)
        }

        moreMoodsRoute {
            MoreMoodsScreen()
        }

        moreAlbumsRoute {
            MoreAlbumsScreen()
        }

        Content {
            Scaffold(
                key = "home",
                topIconButtonId = R.drawable.settings,
                onTopIconButtonClick = { settingsRoute() },
                tabIndex = UIStatePreferences.homeScreenTabIndex,
                onTabChange = { UIStatePreferences.homeScreenTabIndex = it },
                tabColumnContent = {
                    tab(0, R.string.quick_picks, R.drawable.sparkles)
                    tab(1, R.string.playlists, R.drawable.playlist)
                    tab(2, R.string.discover, R.drawable.globe)
                    tab(3, R.string.local, R.drawable.download)
                    tab(4, R.string.settings, R.drawable.settings)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    val onSearchClick: (String) -> Unit = { query ->
                        if (query.isNotBlank()) {
                            searchResultRoute(query)
                        } else {
                            searchRoute("")
                        }
                    }
                    when (currentTabIndex) {
                        0 -> QuickPicks(
                            onAlbumClick = { albumRoute(it.key) },
                            onArtistClick = { artistRoute(it.key) },
                            onPlaylistClick = {
                                playlistRoute(
                                    p0 = it.key,
                                    p1 = null,
                                    p2 = null,
                                    p3 = it.channel?.name == "YouTube Music"
                                )
                            },
                            onSearchClick = { onSearchClick("") },
                            onFavoritesClick = { builtInPlaylistRoute(BuiltInPlaylist.Favorites) },
                            onSeeAllPlaylistsClick = { UIStatePreferences.homeScreenTabIndex = 1 },
                            onSettingsClick = { UIStatePreferences.homeScreenTabIndex = 4 },
                            onMoreMoodsClick = { moreMoodsRoute() },
                            onExploreClick = { UIStatePreferences.homeScreenTabIndex = 2 }
                        )

                        1 -> HomePlaylists(
                            onBuiltInPlaylist = { builtInPlaylistRoute(it) },
                            onPlaylistClick = { localPlaylistRoute(it.id) },
                            onPipedPlaylistClick = { session, playlist ->
                                pipedPlaylistRoute(
                                    p0 = session.apiBaseUrl.toString(),
                                    p1 = session.token,
                                    p2 = playlist.id.toString()
                                )
                            },
                            onSearchClick = { onSearchClick("") }
                        )

                        2 -> HomeDiscovery(
                            onMoodClick = { mood -> moodRoute(mood.toUiMood()) },
                            onNewReleaseAlbumClick = { albumRoute(it) },
                            onSearchClick = onSearchClick,
                            onMoreMoodsClick = { moreMoodsRoute() },
                            onMoreAlbumsClick = { moreAlbumsRoute() },
                            onPlaylistClick = { playlistRoute(it, null, null, true) }
                        )

                        3 -> HomeLocalSongs(
                            onSearchClick = { onSearchClick("") }
                        )

                        4 -> SettingsScreen()
                    }
                }
            }
        }
    }
}
