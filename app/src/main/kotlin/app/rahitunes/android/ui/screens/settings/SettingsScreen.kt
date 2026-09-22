@file:Suppress("TooManyFunctions")

package app.rahitunes.android.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.themed.Header
import app.rahitunes.android.ui.components.themed.NumberFieldDialog
import app.rahitunes.android.ui.components.themed.Scaffold
import app.rahitunes.android.ui.components.themed.Slider
import app.rahitunes.android.ui.components.themed.Switch
import app.rahitunes.android.ui.components.themed.ValueSelectorDialog
import app.rahitunes.android.ui.screens.GlobalRoutes
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.android.utils.color
import app.rahitunes.android.utils.secondary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.compose.persist.PersistMapCleanup
import app.rahitunes.compose.routing.RouteHandler
import app.rahitunes.core.ui.Dimensions
import app.rahitunes.core.ui.LocalAppearance
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import app.rahitunes.android.preferences.UIStatePreferences

@Route
@Composable
fun SettingsScreen() {
    val saveableStateHolder = rememberSaveableStateHolder()
    val (tabIndex, onTabChanged) = rememberSaveable { mutableIntStateOf(0) }

    PersistMapCleanup("settings/")

    val tabs = remember {
        listOf(
            Triple(0, R.string.appearance, R.drawable.color_palette),
            Triple(1, R.string.player, R.drawable.play),
            Triple(2, R.string.cache, R.drawable.server),
            Triple(3, R.string.database, R.drawable.server),
            Triple(4, R.string.sync, R.drawable.sync),
            Triple(5, R.string.other, R.drawable.shapes)
        )
    }

    val (colorPalette, typography) = LocalAppearance.current

    RouteHandler {
        GlobalRoutes()

        Content {
            Column(
                modifier = Modifier
                    .background(colorPalette.background0)
                    .fillMaxSize()
            ) {
                // Top Settings Header with Back Button and Title
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top).asPaddingValues().calculateTopPadding())
                ) {
                    // Navigation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Back Chevron Pill
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colorPalette.background1)
                                .border(1.dp, colorPalette.textSecondary.copy(alpha = 0.15f), CircleShape)
                                .clickable {
                                    if (UIStatePreferences.homeScreenTabIndex == 4) {
                                        UIStatePreferences.homeScreenTabIndex = 0
                                    }
                                    pop()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.chevron_back),
                                contentDescription = "Back",
                                colorFilter = ColorFilter.tint(colorPalette.text),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        BasicText(
                            text = stringResource(R.string.settings),
                            style = typography.l.copy(color = colorPalette.text, fontWeight = FontWeight.Bold)
                        )
                    }

                    // TOP HORIZONTAL CATEGORIES BAR (Appearance, Player, Cache, Database, Sync, Other)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tabs) { (index, titleRes, iconRes) ->
                            val isSelected = tabIndex == index
                            val title = stringResource(titleRes)

                            Box(
                                modifier = Modifier
                                    .shadow(
                                        elevation = if (isSelected) 8.dp else 0.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        spotColor = Color(0xFFE08000).copy(alpha = 0.5f)
                                    )
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) {
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFE08000), Color(0xFFE04000))
                                            )
                                        } else {
                                            androidx.compose.ui.graphics.SolidColor(colorPalette.background1)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color(0xFFE08000) else colorPalette.textSecondary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable { onTabChanged(index) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Image(
                                        painter = painterResource(iconRes),
                                        contentDescription = title,
                                        colorFilter = ColorFilter.tint(
                                            if (isSelected) Color.White else colorPalette.textSecondary
                                        ),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    BasicText(
                                        text = title,
                                        style = typography.xs.copy(
                                            color = if (isSelected) Color.White else colorPalette.text,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Subscreen Content Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = tabIndex,
                        transitionSpec = {
                            val slideDirection = if (targetState > initialState) Left else Right
                            val animationSpec = spring(
                                dampingRatio = 0.9f,
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            )
                            ContentTransform(
                                targetContentEnter = slideIntoContainer(slideDirection, animationSpec),
                                initialContentExit = slideOutOfContainer(slideDirection, animationSpec),
                                sizeTransform = null
                            )
                        },
                        label = "settings_tabs"
                    ) { currentTabIndex ->
                        saveableStateHolder.SaveableStateProvider(currentTabIndex) {
                            when (currentTabIndex) {
                                0 -> AppearanceSettings()
                                1 -> PlayerSettings()
                                2 -> CacheSettings()
                                3 -> DatabaseSettings()
                                4 -> SyncSettings()
                                5 -> OtherSettings()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
inline fun <reified T : Enum<T>> EnumValueSelectorSettingsEntry(
    title: String,
    selectedValue: T,
    noinline onValueSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    noinline valueText: @Composable (T) -> String = { it.name },
    noinline trailingContent: (@Composable () -> Unit)? = null
) = ValueSelectorSettingsEntry(
    title = title,
    selectedValue = selectedValue,
    values = enumValues<T>().toList().toImmutableList(),
    onValueSelect = onValueSelect,
    modifier = modifier,
    isEnabled = isEnabled,
    valueText = valueText,
    trailingContent = trailingContent
)

@Composable
fun <T> ValueSelectorSettingsEntry(
    title: String,
    selectedValue: T,
    values: ImmutableList<T>,
    onValueSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    isEnabled: Boolean = true,
    usePadding: Boolean = true,
    valueText: @Composable (T) -> String = { it.toString() },
    trailingContent: (@Composable () -> Unit)? = null
) {
    var isShowingDialog by remember { mutableStateOf(false) }

    if (isShowingDialog) ValueSelectorDialog(
        onDismiss = { isShowingDialog = false },
        title = title,
        selectedValue = selectedValue,
        values = values,
        onValueSelect = onValueSelect,
        valueText = valueText
    )

    SettingsEntry(
        modifier = modifier,
        title = title,
        text = text ?: valueText(selectedValue),
        onClick = { isShowingDialog = true },
        isEnabled = isEnabled,
        trailingContent = trailingContent,
        usePadding = usePadding
    )
}

@Composable
fun SwitchSettingsEntry(
    title: String,
    text: String?,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    usePadding: Boolean = true
) = SettingsEntry(
    modifier = modifier,
    title = title,
    text = text,
    onClick = { onCheckedChange(!isChecked) },
    isEnabled = isEnabled,
    usePadding = usePadding
) {
    Switch(isChecked = isChecked)
}

@Composable
fun SliderSettingsEntry(
    title: String,
    text: String,
    state: Float,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    onSlide: (Float) -> Unit = { },
    onSlideComplete: () -> Unit = { },
    toDisplay: @Composable (Float) -> String = { it.toString() },
    steps: Int = 0,
    isEnabled: Boolean = true,
    usePadding: Boolean = true,
    showTicks: Boolean = steps != 0
) = Column(modifier = modifier) {
    SettingsEntry(
        title = title,
        text = "$text (${toDisplay(state)})",
        onClick = {},
        isEnabled = isEnabled,
        usePadding = usePadding
    )

    Slider(
        state = state,
        setState = onSlide,
        onSlideComplete = onSlideComplete,
        range = range,
        steps = steps,
        showTicks = showTicks,
        modifier = Modifier
            .height(36.dp)
            .alpha(if (isEnabled) 1f else 0.5f)
            .let { if (usePadding) it.padding(start = 32.dp, end = 16.dp) else it }
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    )
}

@Composable
inline fun IntSettingsEntry(
    title: String,
    text: String,
    currentValue: Int,
    crossinline setValue: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    defaultValue: Int = 0,
    isEnabled: Boolean = true,
    usePadding: Boolean = true
) {
    var isShowingDialog by remember { mutableStateOf(false) }

    if (isShowingDialog) NumberFieldDialog(
        onDismiss = { isShowingDialog = false },
        onAccept = {
            setValue(it)
            isShowingDialog = false
        },
        initialValue = currentValue,
        defaultValue = defaultValue,
        convert = { it.toIntOrNull() },
        range = range
    )

    SettingsEntry(
        modifier = modifier,
        title = title,
        text = text,
        onClick = { isShowingDialog = true },
        isEnabled = isEnabled,
        usePadding = usePadding
    )
}

@Composable
fun SettingsEntry(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    isEnabled: Boolean = true,
    usePadding: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val (colorPalette, typography) = LocalAppearance.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = if (usePadding) 16.dp else 0.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorPalette.background1)
            .border(1.dp, colorPalette.textSecondary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable(enabled = isEnabled, onClick = onClick)
            .alpha(if (isEnabled) 1f else 0.5f)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = title,
                style = typography.xs.copy(
                    color = colorPalette.text,
                    fontWeight = FontWeight.Bold
                )
            )

            if (text != null) BasicText(
                text = text,
                style = typography.xs.copy(
                    color = colorPalette.textSecondary,
                    fontSize = 12.sp
                )
            )
        }

        trailingContent?.invoke()
    }
}

@Composable
fun SettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
    important: Boolean = false
) {
    val (colorPalette, typography) = LocalAppearance.current

    BasicText(
        text = text,
        style = if (important) typography.xxs.semiBold.color(colorPalette.red)
        else typography.xxs.copy(color = colorPalette.textSecondary),
        modifier = modifier
            .padding(start = 16.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
fun SettingsEntryGroupText(
    title: String,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .padding(start = 16.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE08000))
        )
        BasicText(
            text = title.uppercase(),
            style = typography.xxs.copy(
                color = Color(0xFFE08000),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            ),
            modifier = Modifier.semantics { text = AnnotatedString(text = title) }
        )
    }
}

@Composable
fun SettingsGroupSpacer(modifier: Modifier = Modifier) = Spacer(modifier = modifier.height(14.dp))

@Composable
fun SettingsCategoryScreen(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    scrollState: ScrollState? = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current

    Column(
        modifier = modifier
            .background(colorPalette.background0)
            .fillMaxSize()
            .let { if (scrollState != null) it.verticalScroll(state = scrollState) else it }
            .padding(
                LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            )
            .padding(top = 4.dp)
            .padding(bottom = Dimensions.bottomBar.height + Dimensions.bottomBar.bottomMargin + 32.dp)
    ) {
        Header(title = title) {
            description?.let { description ->
                BasicText(
                    text = description,
                    style = typography.s.copy(color = colorPalette.textSecondary)
                )
                SettingsGroupSpacer()
            }
        }

        content()

        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "Made with love by RahiBladeX",
                style = typography.xs.copy(
                    color = Color(0xFFFF9100),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    important: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) = Column(modifier = modifier.fillMaxWidth()) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            SettingsEntryGroupText(title = title)

            description?.let { description ->
                SettingsDescription(
                    text = description,
                    important = important
                )
            }
        }

        trailingContent?.let {
            it()
            Spacer(Modifier.width(16.dp))
        }
    }

    content()

    SettingsGroupSpacer()
}
