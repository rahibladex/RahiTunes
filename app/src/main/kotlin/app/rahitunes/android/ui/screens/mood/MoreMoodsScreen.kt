package app.rahitunes.android.ui.screens.mood

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import app.rahitunes.android.R
import app.rahitunes.android.models.toUiMood
import app.rahitunes.android.ui.components.themed.Scaffold
import app.rahitunes.android.ui.screens.GlobalRoutes
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.android.ui.screens.moodRoute
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.routing.RouteHandler

@Route
@Composable
fun MoreMoodsScreen() {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup(prefix = "more_moods/")

    RouteHandler {
        GlobalRoutes()

        moodRoute { mood ->
            MoodScreen(mood = mood)
        }

        Content {
            Scaffold(
                key = "moremoods",
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                tabIndex = 0,
                onTabChange = { },
                tabColumnContent = {
                    tab(0, R.string.moods_and_genres, R.drawable.playlist)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    when (currentTabIndex) {
                        0 -> MoreMoodsList(
                            onMoodClick = { mood -> moodRoute(mood.toUiMood()) }
                        )
                    }
                }
            }
        }
    }
}
