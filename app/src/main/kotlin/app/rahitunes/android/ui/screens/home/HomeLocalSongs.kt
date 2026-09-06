package app.rahitunes.android.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.Database
import app.rahitunes.android.R
import app.rahitunes.android.models.Song
import app.rahitunes.android.preferences.OrderPreferences
import app.rahitunes.android.service.LOCAL_KEY_PREFIX
import app.rahitunes.android.transaction
import app.rahitunes.android.ui.screens.Route
import app.rahitunes.android.utils.AudioMediaCursor
import app.rahitunes.android.utils.hasPermission
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.core.ui.utils.isAtLeastAndroid13
import app.rahitunes.core.ui.utils.isCompositionLaunched
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val permission = if (isAtLeastAndroid13) Manifest.permission.READ_MEDIA_AUDIO
else Manifest.permission.READ_EXTERNAL_STORAGE

@Route
@Composable
fun HomeLocalSongs(onSearchClick: () -> Unit) = with(OrderPreferences) {
    val context = LocalContext.current
    val (colorPalette, typography) = LocalAppearance.current

    var hasPermission by remember(isCompositionLaunched()) {
        mutableStateOf(context.applicationContext.hasPermission(permission))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermission = it }
    )

    LaunchedEffect(hasPermission) {
        if (hasPermission) context.musicFilesAsFlow().collect()
    }

    if (hasPermission) HomeSongs(
        onSearchClick = onSearchClick,
        songProvider = {
            Database.songs(
                sortBy = localSongSortBy,
                sortOrder = localSongSortOrder,
                isLocal = true
            ).map { songs -> songs.filter { it.durationText != "0:00" } }
        },
        sortBy = localSongSortBy,
        setSortBy = { localSongSortBy = it },
        sortOrder = localSongSortOrder,
        setSortOrder = { localSongSortOrder = it },
        title = "Local Vault"
    ) else {
        LaunchedEffect(Unit) { launcher.launch(permission) }

        Column(
            modifier = Modifier
                .background(colorPalette.background0)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cyber Vault Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF11141D))
                    .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.download),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color(0xFF00F0FF)),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            BasicText(
                text = "Local Storage Vault",
                style = typography.m.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            BasicText(
                text = "Grant audio storage permissions to index and play lossless FLAC, MP3, and WAV tracks from your device.",
                style = typography.xs.copy(
                    color = Color(0xFF8E9AA8),
                    fontSize = 13.sp
                ),
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00F0FF), Color(0xFFFF0055))
                        )
                    )
                    .clickable {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                BasicText(
                    text = "Authorize Storage Access",
                    style = typography.xs.copy(
                        color = Color(0xFF08090C),
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

private val mediaScope = CoroutineScope(Dispatchers.IO + CoroutineName("MediaStore worker"))
fun Context.musicFilesAsFlow(): StateFlow<List<Song>> = flow {
    var version: String? = null

    while (currentCoroutineContext().isActive) {
        val newVersion = MediaStore.getVersion(applicationContext)

        if (version != newVersion) {
            version = newVersion

            AudioMediaCursor.query(contentResolver) {
                buildList {
                    while (next()) {
                        if (!isMusic || duration == 0) continue
                        add(
                            Song(
                                id = "$LOCAL_KEY_PREFIX$id",
                                title = name,
                                artistsText = artist,
                                durationText = duration.milliseconds.toComponents { minutes, seconds, _ ->
                                    "$minutes:${seconds.toString().padStart(2, '0')}"
                                },
                                thumbnailUrl = albumUri.toString()
                            )
                        )
                    }
                }
            }?.let { emit(it) }
        }
        delay(5.seconds)
    }
}.distinctUntilChanged()
    .onEach { songs -> transaction { songs.forEach(Database::insert) } }
    .stateIn(mediaScope, SharingStarted.Eagerly, listOf())
