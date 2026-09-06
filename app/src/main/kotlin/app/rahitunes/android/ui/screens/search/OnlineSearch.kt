package app.rahitunes.android.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.LocalPinnableContainer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import app.rahitunes.android.utils.bold
import app.rahitunes.android.utils.color
import app.rahitunes.core.ui.utils.roundedShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rahitunes.android.Database
import app.rahitunes.android.LocalPlayerAwareWindowInsets
import app.rahitunes.android.R
import app.rahitunes.android.models.SearchQuery
import app.rahitunes.android.preferences.DataPreferences
import app.rahitunes.android.query
import app.rahitunes.android.ui.components.themed.FloatingActionsContainerWithScrollToTop
import app.rahitunes.android.ui.components.themed.Header
import app.rahitunes.android.ui.components.themed.SecondaryTextButton
import app.rahitunes.android.utils.align
import app.rahitunes.android.utils.center
import app.rahitunes.android.utils.disabled
import app.rahitunes.android.utils.medium
import app.rahitunes.android.utils.secondary
import app.rahitunes.compose.persist.persist
import app.rahitunes.compose.persist.persistList
import app.rahitunes.core.ui.LocalAppearance
import app.rahitunes.providers.innertube.Innertube
import app.rahitunes.providers.innertube.models.bodies.SearchSuggestionsBody
import app.rahitunes.providers.innertube.requests.searchSuggestions
import io.ktor.http.Url
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun OnlineSearch(
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
    onSearch: (String) -> Unit,
    onViewPlaylist: (String) -> Unit,
    decorationBox: @Composable (@Composable () -> Unit) -> Unit,
    focused: Boolean,
    modifier: Modifier = Modifier
) = Box(modifier = modifier) {
    val (colorPalette, typography) = LocalAppearance.current

    var history by persistList<SearchQuery>("search/online/history")
    var suggestionsResult by persist<Result<List<String>?>?>("search/online/suggestionsResult")

    LaunchedEffect(textFieldValue.text) {
        if (DataPreferences.pauseSearchHistory) return@LaunchedEffect

        Database.queries("%${textFieldValue.text}%")
            .distinctUntilChanged { old, new -> old.size == new.size }
            .collect { history = it.toImmutableList() }
    }

    LaunchedEffect(textFieldValue.text) {
        if (textFieldValue.text.isEmpty()) return@LaunchedEffect

        delay(500)
        suggestionsResult = Innertube.searchSuggestions(
            body = SearchSuggestionsBody(input = textFieldValue.text)
        )
    }

    val playlistId = remember(textFieldValue.text) {
        runCatching {
            Url(textFieldValue.text).takeIf {
                it.host.endsWith("youtube.com", ignoreCase = true) &&
                    it.segments.lastOrNull()?.equals("playlist", ignoreCase = true) == true
            }?.parameters?.get("list")
        }.getOrNull()
    }

    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        contentPadding = LocalPlayerAwareWindowInsets.current
            .only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
            .asPaddingValues(),
        modifier = Modifier.fillMaxSize()
    ) {
        item(
            key = "header",
            contentType = 0
        ) {
            val container = LocalPinnableContainer.current

            DisposableEffect(Unit) {
                val handle = container?.pin()

                onDispose {
                    handle?.release()
                }
            }

            LaunchedEffect(focused) {
                if (!focused) return@LaunchedEffect

                delay(300)
                focusRequester.requestFocus()
            }

            Header(
                titleContent = {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = onTextFieldValueChange,
                        textStyle = typography.xxl.medium.align(TextAlign.End),
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (textFieldValue.text.isNotEmpty()) onSearch(textFieldValue.text)
                            }
                        ),
                        cursorBrush = SolidColor(colorPalette.text),
                        decorationBox = decorationBox,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                },
                actionsContent = {
                    if (playlistId != null) {
                        val isAlbum = playlistId.startsWith("OLAK5uy_")

                        SecondaryTextButton(
                            text = if (isAlbum) stringResource(R.string.view_album)
                            else stringResource(R.string.view_playlist),
                            onClick = { onViewPlaylist(textFieldValue.text) }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (textFieldValue.text.isNotEmpty()) SecondaryTextButton(
                        text = stringResource(R.string.clear),
                        onClick = { onTextFieldValueChange(TextFieldValue()) }
                    )
                }
            )
        }

        items(
            items = history,
            key = SearchQuery::id
        ) { searchQuery ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onSearch(searchQuery.query) }
                    .fillMaxWidth()
                    .padding(all = 16.dp)
                    .animateItem()
            ) {
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp)
                        .paint(
                            painter = painterResource(R.drawable.time),
                            colorFilter = ColorFilter.disabled
                        )
                )

                BasicText(
                    text = searchQuery.query,
                    style = typography.s.secondary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )

                Image(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    colorFilter = ColorFilter.disabled,
                    modifier = Modifier
                        .clickable(
                            indication = ripple(bounded = false),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                query {
                                    Database.delete(searchQuery)
                                }
                            }
                        )
                        .padding(horizontal = 8.dp)
                        .size(20.dp)
                )

                Image(
                    painter = painterResource(R.drawable.arrow_forward),
                    contentDescription = null,
                    colorFilter = ColorFilter.disabled,
                    modifier = Modifier
                        .clickable(
                            indication = ripple(bounded = false),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                onTextFieldValueChange(
                                    TextFieldValue(
                                        text = searchQuery.query,
                                        selection = TextRange(searchQuery.query.length)
                                    )
                                )
                            }
                        )
                        .rotate(225f)
                        .padding(horizontal = 8.dp)
                        .size(22.dp)
                )
            }
        }

        if (textFieldValue.text.isEmpty()) {
            item(key = "browse_categories_title") {
                BasicText(
                    text = "Browse Categories",
                    style = typography.m.copy(
                        color = colorPalette.text,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            val genreCards = listOf(
                Pair("Pop Velocity", listOf(Color(0xFFFF0055), Color(0xFF7928CA))),
                Pair("Cyber Electronic", listOf(Color(0xFF00F0FF), Color(0xFF0070F3))),
                Pair("Hip-Hop & Trap", listOf(Color(0xFFFF9900), Color(0xFFFF0055))),
                Pair("Lo-Fi & Chillwave", listOf(Color(0xFF7928CA), Color(0xFF00DFD8))),
                Pair("Deep House", listOf(Color(0xFF00F0FF), Color(0xFF1F2637))),
                Pair("Synthwave 80s", listOf(Color(0xFFFF0080), Color(0xFF7928CA))),
                Pair("Workout Matrix", listOf(Color(0xFFFF4D4D), Color(0xFFF9CB28))),
                Pair("Focus Coding", listOf(Color(0xFF00DFD8), Color(0xFF007CF0)))
            )

            item(key = "browse_categories_grid") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in genreCards.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val card1 = genreCards[i]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(84.dp)
                                    .clip(16.dp.roundedShape)
                                    .background(Brush.linearGradient(card1.second))
                                    .clickable { onSearch(card1.first.split(" ").first()) }
                                    .padding(14.dp)
                            ) {
                                BasicText(
                                    text = card1.first,
                                    style = typography.s.bold.color(Color.White),
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                            }

                            if (i + 1 < genreCards.size) {
                                val card2 = genreCards[i + 1]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(84.dp)
                                        .clip(16.dp.roundedShape)
                                        .background(Brush.linearGradient(card2.second))
                                        .clickable { onSearch(card2.first.split(" ").first()) }
                                        .padding(14.dp)
                                ) {
                                    BasicText(
                                        text = card2.first,
                                        style = typography.s.bold.color(Color.White),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        suggestionsResult?.getOrNull()?.let { suggestions ->
            items(items = suggestions) { suggestion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onSearch(suggestion) }
                        .fillMaxWidth()
                        .padding(all = 16.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(20.dp)
                            .paint(
                                painter = painterResource(R.drawable.search),
                                colorFilter = ColorFilter.disabled
                            )
                    )

                    BasicText(
                        text = suggestion,
                        style = typography.s.secondary,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                    )

                    Image(
                        painter = painterResource(R.drawable.arrow_forward),
                        contentDescription = null,
                        colorFilter = ColorFilter.disabled,
                        modifier = Modifier
                            .clickable(
                                indication = ripple(bounded = false),
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    onTextFieldValueChange(
                                        TextFieldValue(
                                            text = suggestion,
                                            selection = TextRange(suggestion.length)
                                        )
                                    )
                                }
                            )
                            .rotate(225f)
                            .padding(horizontal = 8.dp)
                            .size(22.dp)
                    )
                }
            }
        } ?: suggestionsResult?.exceptionOrNull()?.let {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    BasicText(
                        text = stringResource(R.string.error_message),
                        style = typography.s.secondary.center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    FloatingActionsContainerWithScrollToTop(lazyListState = lazyListState)
}
