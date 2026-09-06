package app.rahitunes.android.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.models.Album
import app.rahitunes.android.ui.components.themed.TextPlaceholder
import app.rahitunes.android.utils.secondary
import app.rahitunes.android.utils.semiBold
import app.rahitunes.android.utils.thumbnail
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.shimmer
import app.rahitunes.core.ui.utils.px
import app.rahitunes.providers.innertube.Innertube
import coil3.compose.AsyncImage

@Composable
fun AlbumItem(
    album: Album,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = AlbumItem(
    thumbnailUrl = album.thumbnailUrl,
    title = album.title,
    authors = album.authorsText,
    year = album.year,
    thumbnailSize = thumbnailSize,
    alternative = alternative,
    modifier = modifier
)

@Composable
fun AlbumItem(
    album: Innertube.AlbumItem,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = AlbumItem(
    thumbnailUrl = album.thumbnail?.url,
    title = album.info?.name,
    authors = album.authors?.joinToString("") { it.name.orEmpty() },
    year = album.year,
    thumbnailSize = thumbnailSize,
    alternative = alternative,
    modifier = modifier
)

@Composable
fun AlbumItem(
    thumbnailUrl: String?,
    title: String?,
    authors: String?,
    year: String?,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = ItemContainer(
    alternative = alternative,
    thumbnailSize = thumbnailSize,
    modifier = modifier
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF0F121C).copy(alpha = 0.7f))
        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
        .padding(if (alternative) 6.dp else 4.dp)
) {
    val typography = LocalAppearance.current.typography

    Box(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = Color(0xFF8B5CF6).copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .size(thumbnailSize)
    ) {
        AsyncImage(
            model = thumbnailUrl?.thumbnail(thumbnailSize.px),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(thumbnailSize)
        )
    }

    ItemInfoContainer {
        title?.let {
            BasicText(
                text = title,
                style = typography.xs.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = if (alternative) 1 else 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!alternative) authors?.let {
            BasicText(
                text = authors,
                style = typography.xs.copy(
                    color = Color(0xFF8E9AA8),
                    fontSize = 12.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        year?.let {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF8B5CF6).copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                BasicText(
                    text = year,
                    style = typography.xxs.copy(
                        color = Color(0xFFC4B5FD),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AlbumItemPlaceholder(
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = ItemContainer(
    alternative = alternative,
    thumbnailSize = thumbnailSize,
    modifier = modifier
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF0F121C).copy(alpha = 0.7f))
        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
        .padding(if (alternative) 6.dp else 4.dp)
) {
    val colorPalette = LocalAppearance.current.colorPalette

    Spacer(
        modifier = Modifier
            .background(color = colorPalette.shimmer, shape = RoundedCornerShape(14.dp))
            .size(thumbnailSize)
    )

    ItemInfoContainer {
        TextPlaceholder()
        if (!alternative) TextPlaceholder()
        TextPlaceholder(modifier = Modifier.padding(top = 4.dp))
    }
}
