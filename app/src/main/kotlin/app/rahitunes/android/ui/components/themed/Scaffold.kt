package app.rahitunes.android.ui.components.themed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Up
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.R
import app.rahitunes.android.preferences.UIStatePreferences
import app.rahitunes.core.ui.LocalAppearance

@Composable
fun Scaffold(
    key: String,
    topIconButtonId: Int,
    onTopIconButtonClick: () -> Unit,
    tabIndex: Int,
    onTabChange: (Int) -> Unit,
    tabColumnContent: TabsBuilder.() -> Unit,
    modifier: Modifier = Modifier,
    tabsEditingTitle: String = stringResource(R.string.tabs),
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val (colorPalette) = LocalAppearance.current
    var hiddenTabs by UIStatePreferences.mutableTabStateOf(key)
    val tabs = TabsBuilder.rememberTabs(key, tabColumnContent)

    if (key == "home") {
        Box(
            modifier = modifier
                .background(colorPalette.background0)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = tabIndex,
                transitionSpec = {
                    val slideDirection = if (targetState > initialState) Up else Down
                    val animationSpec = spring(
                        dampingRatio = 0.9f,
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntOffset.VisibilityThreshold
                    )

                    ContentTransform(
                        targetContentEnter = slideIntoContainer(slideDirection, animationSpec),
                        initialContentExit = slideOutOfContainer(slideDirection, animationSpec),
                        sizeTransform = null
                    )
                },
                content = content,
                label = "home_tabs"
            )
        }
    } else {
        val currentTab = tabs.getOrNull(tabIndex) ?: tabs.firstOrNull()
        val headerTitle = currentTab?.title?.invoke().orEmpty()

        Column(
            modifier = modifier
                .background(colorPalette.background0)
                .fillMaxSize()
        ) {
            // Top Navigation Bar for Sub-screens: Back Button + Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top).asPaddingValues().calculateTopPadding())
            ) {
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
                            .border(1.dp, colorPalette.textSecondary.copy(alpha = 0.2f), CircleShape)
                            .clickable(onClick = onTopIconButtonClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(topIconButtonId),
                            contentDescription = "Back",
                            colorFilter = ColorFilter.tint(colorPalette.text),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (headerTitle.isNotBlank()) {
                        BasicText(
                            text = headerTitle,
                            style = LocalAppearance.current.typography.l.copy(
                                color = colorPalette.text,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // If multiple tabs exist, render horizontal tab pills
                if (tabs.size > 1) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(tabs) { index, tab ->
                            val isSelected = tabIndex == index
                            val title = tab.title()

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
                                    .clickable { onTabChange(index) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Image(
                                        painter = painterResource(tab.icon),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(if (isSelected) Color.White else colorPalette.textSecondary),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    BasicText(
                                        text = title,
                                        style = LocalAppearance.current.typography.xs.copy(
                                            color = if (isSelected) Color.White else colorPalette.textSecondary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = tabIndex,
                    transitionSpec = {
                        val slideDirection = if (targetState > initialState) Up else Down
                        val animationSpec = spring(
                            dampingRatio = 0.9f,
                            stiffness = Spring.StiffnessLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )

                        ContentTransform(
                            targetContentEnter = slideIntoContainer(slideDirection, animationSpec),
                            initialContentExit = slideOutOfContainer(slideDirection, animationSpec),
                            sizeTransform = null
                        )
                    },
                    content = content,
                    label = "subscreen_tabs"
                )
            }
        }
    }
}
