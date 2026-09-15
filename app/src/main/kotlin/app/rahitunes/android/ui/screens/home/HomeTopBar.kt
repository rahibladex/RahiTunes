package app.rahitunes.android.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.R
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.android.utils.bold
import app.rahitunes.android.utils.color
import app.rahitunes.android.utils.medium
import app.rahitunes.core.ui.LocalAppearance

@Composable
fun HomeTopBar(
    userName: String = "Alex",
    selectedFilter: String,
    filterOptions: List<String> = listOf("All", "Music", "Podcasts", "Liked Songs", "Discover", "Made for You"),
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onFilterSelected: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSearchClick: (String) -> Unit = {},
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF381512), Color(0xFF0E0E18))
                )
            )
            .padding(top = 28.dp, bottom = 8.dp)
    ) {
        // Top Command Row: Orange/Red Gradient Accent Header + Greeting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Custom RahiTunes Text Branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clickable(onClick = onAvatarClick)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    // "Rahi" (Smooth Orange-to-Red Gradient Fade) + "Tunes" (White)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicText(
                            text = "Rahi",
                            style = typography.m.copy(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFF9100), Color(0xFFFF1212))
                                ),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        )
                        BasicText(
                            text = "Tunes",
                            style = typography.m.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        )
                    }

                    BasicText(
                        text = "THE MUSIC APP U DESERVE",
                        style = typography.xxs.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            letterSpacing = 1.2.sp
                        )
                    )
                }
            }

            // Right: Orange Favorites Action Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF151528))
                    .clickable(onClick = onFavoritesClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.heart),
                        contentDescription = "Favorites",
                        colorFilter = ColorFilter.tint(Color(0xFFFF9100)),
                        modifier = Modifier.size(14.dp)
                    )
                    BasicText(
                        text = "Liked Songs",
                        maxLines = 1,
                        style = typography.xxs.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Interactive Search Bar Capsule (#FF9100 to #FF1212 Gradient Accent)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF151528))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFFF9100).copy(alpha = 0.4f), Color(0xFFFF1212).copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search",
                    colorFilter = ColorFilter.tint(Color(0xFFFF9100)),
                    modifier = Modifier.size(18.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        BasicText(
                            text = "What do you want to listen to?",
                            maxLines = 1,
                            style = typography.xs.copy(
                                color = Color(0xFFA2A2D0),
                                fontSize = 13.sp
                            )
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { newQuery ->
                            onSearchQueryChange(newQuery)
                            onSearchClick(newQuery)
                        },
                        singleLine = true,
                        maxLines = 1,
                        textStyle = typography.xs.copy(
                            color = Color.White,
                            fontSize = 13.sp
                        ),
                        cursorBrush = SolidColor(Color(0xFFFF9100)),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    focusManager.clearFocus()
                                    onSearchClick(searchQuery.trim())
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Clear",
                        colorFilter = ColorFilter.tint(Color(0xFFA2A2D0)),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                onSearchQueryChange("")
                                onSearchClick("")
                            }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Horizontal Category Filter Pills (Gradient Selected Pill)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            filterOptions.forEach { filter ->
                val isSelected = filter == selectedFilter

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(Color(0xFFFF9100), Color(0xFFFF1212)))
                            else Brush.horizontalGradient(listOf(Color(0xFF151528), Color(0xFF151528)))
                        )
                        .clickable { onFilterSelected(filter) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = filter,
                        style = if (isSelected) typography.xs.bold.color(Color.White) else typography.xs.medium.color(Color(0xFFA2A2D0))
                    )
                }
            }
        }
    }
}
