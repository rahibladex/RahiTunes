package app.rahitunes.android.ui.screens.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.themed.Scaffold
import app.rahitunes.android.ui.screens.GlobalRoutes
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.routing.RouteHandler

@Route
@Composable
fun PlaylistScreen(
    browseId: String,
    params: String?,
    shouldDedup: Boolean,
    maxDepth: Int? = null
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    PersistMapCleanup(prefix = "playlist/$browseId")

    RouteHandler {
        GlobalRoutes()

        Content {
            Scaffold(
                key = "playlist",
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                tabIndex = 0,
                onTabChange = { },
                tabColumnContent = {
                    tab(0, R.string.songs, R.drawable.musical_notes)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    when (currentTabIndex) {
                        0 -> PlaylistSongList(
                            browseId = browseId,
                            params = params,
                            maxDepth = maxDepth,
                            shouldDedup = shouldDedup
                        )
                    }
                }
            }
        }
    }
}
