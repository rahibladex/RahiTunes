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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.Database
import app.rahitunes.android.R
import app.rahitunes.android.models.Album
import app.rahitunes.android.preferences.OrderPreferences
import app.rahitunes.android.ui.components.themed.HeaderIconButton
import app.rahitunes.android.ui.items.AlbumItem
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.compose.persist.persist
import app.rahitunes.core.data.enums.AlbumSortBy
import app.rahitunes.core.data.enums.SortOrder
import app.rahitunes.core.ui.Dimensions

@Route
@Composable
fun HomeAlbums(
    onAlbumClick: (Album) -> Unit,
    onSearchClick: () -> Unit
) = with(OrderPreferences) {
    var items by persist<List<Album>>(tag = "home/albums", emptyList())

    LaunchedEffect(albumSortBy, albumSortOrder) {
        Database.albums(albumSortBy, albumSortOrder).collect { items = it }
    }

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (albumSortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = ""
    )

    val lazyListState = rememberLazyListState()

    val cardBg = Color(0xFF11141D)
    val cardBorder = Color(0xFF1F2637)
    val neonCyan = Color(0xFF00F0FF)
    val neonPink = Color(0xFFFF0055)
    val slateMuted = Color(0xFF8E9AA8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090C))
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 10.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "albums_header", contentType = 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    // Search Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(cardBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
                            .clickable(onClick = onSearchClick)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(neonCyan, neonPink))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Search albums & discography",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "STUDIO RELEASES • EPs • SINGLES",
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ALBUM DISCOGRAPHY",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "${items.size} studio records indexed",
                                color = slateMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Sorting Icons
                        HeaderIconButton(
                            icon = R.drawable.calendar,
                            enabled = albumSortBy == AlbumSortBy.Year,
                            onClick = { albumSortBy = AlbumSortBy.Year }
                        )

                        HeaderIconButton(
                            icon = R.drawable.text,
                            enabled = albumSortBy == AlbumSortBy.Title,
                            onClick = { albumSortBy = AlbumSortBy.Title }
                        )

                        HeaderIconButton(
                            icon = R.drawable.time,
                            enabled = albumSortBy == AlbumSortBy.DateAdded,
                            onClick = { albumSortBy = AlbumSortBy.DateAdded }
                        )

                        HeaderIconButton(
                            icon = R.drawable.arrow_up,
                            color = neonCyan,
                            onClick = { albumSortOrder = !albumSortOrder },
                            modifier = Modifier.graphicsLayer { rotationZ = sortOrderIconRotation }
                        )
                    }
                }
            }

            items(
                items = items,
                key = Album::id
            ) { album ->
                AlbumItem(
                    album = album,
                    thumbnailSize = Dimensions.thumbnails.album,
                    modifier = Modifier
                        .clickable(onClick = { onAlbumClick(album) })
                        .animateItem()
                )
            }
        }
    }
}
