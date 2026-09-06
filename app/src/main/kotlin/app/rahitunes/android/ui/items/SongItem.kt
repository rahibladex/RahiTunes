package app.rahitunes.android.ui.items

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import app.rahitunes.android.R
import app.rahitunes.android.models.Song
import app.rahitunes.android.preferences.AppearancePreferences
import app.rahitunes.android.ui.components.MusicBars
import app.rahitunes.android.ui.components.themed.TextPlaceholder
import app.rahitunes.android.utils.medium
import app.rahitunes.android.utils.secondary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.android.utils.thumbnail
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.shimmer
import app.rahitunes.core.ui.utils.px
import app.rahitunes.core.ui.utils.songBundle
import app.rahitunes.providers.innertube.Innertube
import coil3.compose.AsyncImage

@Composable
fun SongItem(
    song: Innertube.SongItem,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    showDuration: Boolean = true,
    clip: Boolean = true,
    isPlaying: Boolean = false,
    hideExplicit: Boolean = AppearancePreferences.hideExplicit
) = SongItem(
    modifier = modifier,
    thumbnailUrl = song.thumbnail?.size(thumbnailSize.px),
    title = song.info?.name,
    authors = song.authors?.joinToString("") { it.name.orEmpty() },
    duration = song.durationText,
    explicit = song.explicit,
    thumbnailSize = thumbnailSize,
    showDuration = showDuration,
    clip = clip,
    isPlaying = isPlaying,
    hideExplicit = hideExplicit
)

@Composable
fun SongItem(
    song: MediaItem,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    onThumbnailContent: (@Composable BoxScope.() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    showDuration: Boolean = true,
    clip: Boolean = true,
    isPlaying: Boolean = false,
    hideExplicit: Boolean = AppearancePreferences.hideExplicit
) {
    val extras = remember(song) { song.mediaMetadata.extras?.songBundle }

    SongItem(
        modifier = modifier,
        thumbnailUrl = song.mediaMetadata.artworkUri.thumbnail(thumbnailSize.px)?.toString(),
        title = song.mediaMetadata.title?.toString(),
        authors = song.mediaMetadata.artist?.toString(),
        duration = extras?.durationText,
        explicit = extras?.explicit == true,
        thumbnailSize = thumbnailSize,
        onThumbnailContent = onThumbnailContent,
        trailingContent = trailingContent,
        showDuration = showDuration,
        clip = clip,
        isPlaying = isPlaying,
        hideExplicit = hideExplicit
    )
}

@Composable
fun SongItem(
    song: Song,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    index: Int? = null,
    onThumbnailContent: @Composable (BoxScope.() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    showDuration: Boolean = true,
    clip: Boolean = true,
    isPlaying: Boolean = false,
    hideExplicit: Boolean = AppearancePreferences.hideExplicit
) = SongItem(
    modifier = modifier,
    index = index,
    thumbnailUrl = song.thumbnailUrl?.thumbnail(thumbnailSize.px),
    title = song.title,
    authors = song.artistsText,
    duration = song.durationText,
    explicit = song.explicit,
    thumbnailSize = thumbnailSize,
    onThumbnailContent = onThumbnailContent,
    trailingContent = trailingContent,
    showDuration = showDuration,
    clip = clip,
    isPlaying = isPlaying,
    hideExplicit = hideExplicit
)

@Composable
private fun SongItem(
    thumbnailUrl: String?,
    title: String?,
    authors: String?,
    duration: String?,
    explicit: Boolean,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    index: Int? = null,
    onThumbnailContent: @Composable (BoxScope.() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    showDuration: Boolean = true,
    clip: Boolean = true,
    isPlaying: Boolean = false,
    hideExplicit: Boolean = AppearancePreferences.hideExplicit
) {
    val (colorPalette, typography, _, thumbnailShape) = LocalAppearance.current

    SongItem(
        title = title,
        authors = authors,
        duration = duration,
        explicit = explicit,
        thumbnailSize = thumbnailSize,
        thumbnailContent = {
            Box(
                modifier = Modifier
                    .clip(thumbnailShape)
                    .background(colorPalette.background1)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        MusicBars(
                            color = Color(0xFF00F0FF),
                            modifier = Modifier.size(20.dp, 16.dp)
                        )
                    }
                } else if (index != null) {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Black.copy(alpha = 0.7f))
                            .fillMaxSize()
                    )
                    BasicText(
                        text = "${index + 1}",
                        style = typography.xs.semiBold.copy(color = Color.White),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            onThumbnailContent?.invoke(this)
        },
        modifier = modifier,
        trailingContent = trailingContent,
        showDuration = showDuration,
        clip = clip,
        isPlaying = isPlaying,
        hideExplicit = hideExplicit
    )
}

@Composable
private fun SongItem(
    title: String?,
    authors: String?,
    duration: String?,
    explicit: Boolean,
    thumbnailSize: Dp,
    thumbnailContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    showDuration: Boolean = true,
    clip: Boolean = true,
    isPlaying: Boolean = false,
    hideExplicit: Boolean = AppearancePreferences.hideExplicit
) {
    val (colorPalette, typography) = LocalAppearance.current

    val cardBackground = if (isPlaying) {
        Color(0xFF131A2B)
    } else {
        Color(0xFF0C0F18).copy(alpha = 0.6f)
    }

    val cardBorder = if (isPlaying) {
        Color(0xFF00F0FF).copy(alpha = 0.4f)
    } else {
        Color.White.copy(alpha = 0.05f)
    }

    if (!(hideExplicit && explicit)) ItemContainer(
        alternative = false,
        thumbnailSize = thumbnailSize,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier.size(thumbnailSize),
            content = thumbnailContent
        )

        ItemInfoContainer {
            trailingContent?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BasicText(
                        text = title.orEmpty(),
                        style = typography.xs.semiBold.copy(
                            color = if (isPlaying) Color(0xFF00F0FF) else Color.White,
                            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    it()
                }
            } ?: BasicText(
                text = title.orEmpty(),
                style = typography.xs.semiBold.copy(
                    color = if (isPlaying) Color(0xFF00F0FF) else Color.White,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    authors?.let {
                        BasicText(
                            text = authors,
                            style = typography.xs.copy(
                                color = Color(0xFF8E9AA8),
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(
                                weight = 1f,
                                fill = false
                            )
                        )
                    }

                    if (explicit) Image(
                        painter = painterResource(R.drawable.explicit),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color(0xFF8E9AA8)),
                        modifier = Modifier.size(13.dp)
                    )
                }

                if (showDuration) duration?.let {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        BasicText(
                            text = duration,
                            style = typography.xxs.copy(
                                color = if (isPlaying) Color(0xFF00F0FF) else Color(0xFF8E9AA8),
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    } else Unit
}

@Composable
fun SongItemPlaceholder(
    thumbnailSize: Dp,
    modifier: Modifier = Modifier
) = ItemContainer(
    alternative = false,
    thumbnailSize = thumbnailSize,
    modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF0C0F18).copy(alpha = 0.6f))
        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
        .padding(horizontal = 4.dp, vertical = 2.dp)
) {
    val (colorPalette, _, _, thumbnailShape) = LocalAppearance.current

    Spacer(
        modifier = Modifier
            .background(color = colorPalette.shimmer, shape = thumbnailShape)
            .size(thumbnailSize)
    )

    ItemInfoContainer {
        TextPlaceholder()
        TextPlaceholder()
    }
}
