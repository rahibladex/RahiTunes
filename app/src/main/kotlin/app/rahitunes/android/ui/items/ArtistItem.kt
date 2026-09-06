package app.rahitunes.android.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import app.rahitunes.android.models.Artist
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
fun ArtistItem(
    artist: Artist,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = ArtistItem(
    thumbnailUrl = artist.thumbnailUrl,
    name = artist.name,
    subscribersCount = null,
    thumbnailSize = thumbnailSize,
    modifier = modifier,
    alternative = alternative
)

@Composable
fun ArtistItem(
    artist: Innertube.ArtistItem,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = ArtistItem(
    thumbnailUrl = artist.thumbnail?.url,
    name = artist.info?.name,
    subscribersCount = artist.subscribersCountText,
    thumbnailSize = thumbnailSize,
    modifier = modifier,
    alternative = alternative
)

@Composable
fun ArtistItem(
    thumbnailUrl: String?,
    name: String?,
    subscribersCount: String?,
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) = ItemContainer(
    alternative = alternative,
    thumbnailSize = thumbnailSize,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF0F121C).copy(alpha = 0.6f))
        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
        .padding(if (alternative) 6.dp else 4.dp)
) {
    val (_, typography) = LocalAppearance.current

    // Glowing Neon Gradient Ring Frame
    Box(
        modifier = Modifier
            .size(thumbnailSize)
            .clip(CircleShape)
            .background(
                Brush.sweepGradient(
                    listOf(
                        Color(0xFF00F0FF),
                        Color(0xFFFF0055),
                        Color(0xFF8B5CF6),
                        Color(0xFF00F0FF)
                    )
                )
            )
            .padding(2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = thumbnailUrl?.thumbnail(thumbnailSize.px),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .requiredSize(thumbnailSize - 5.dp)
        )
    }

    ItemInfoContainer(
        horizontalAlignment = if (alternative) Alignment.CenterHorizontally else Alignment.Start
    ) {
        BasicText(
            text = name.orEmpty(),
            style = typography.xs.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            maxLines = if (alternative) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )

        subscribersCount?.let {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF0055).copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                BasicText(
                    text = subscribersCount,
                    style = typography.xxs.copy(
                        color = Color(0xFFFF7597),
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
fun ArtistItemPlaceholder(
    thumbnailSize: Dp,
    modifier: Modifier = Modifier,
    alternative: Boolean = false
) {
    val (colorPalette) = LocalAppearance.current

    ItemContainer(
        alternative = alternative,
        thumbnailSize = thumbnailSize,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F121C).copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(if (alternative) 6.dp else 4.dp)
    ) {
        Spacer(
            modifier = Modifier
                .background(color = colorPalette.shimmer, shape = CircleShape)
                .size(thumbnailSize)
        )

        ItemInfoContainer(
            horizontalAlignment = if (alternative) Alignment.CenterHorizontally else Alignment.Start
        ) {
            TextPlaceholder()
            TextPlaceholder(modifier = Modifier.padding(top = 4.dp))
        }
    }
}
