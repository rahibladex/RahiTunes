package app.rahitunes.android.ui.screens.mood

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import app.rahitunes.android.R
import app.rahitunes.android.models.Mood
import app.rahitunes.android.ui.components.themed.Scaffold
import app.rahitunes.android.ui.screens.GlobalRoutes
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.routing.RouteHandler

@Route
@Composable
fun MoodScreen(mood: Mood) {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup(prefix = "playlist/mood/")

    RouteHandler {
        GlobalRoutes()

        Content {
            Scaffold(
                key = "mood",
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                tabIndex = 0,
                onTabChange = { },
                tabColumnContent = {
                    tab(0, R.string.mood, R.drawable.disc)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    when (currentTabIndex) {
                        0 -> MoodList(mood = mood)
                    }
                }
            }
        }
    }
}
