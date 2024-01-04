package com.joshrojas.photosearch.view.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.joshrojas.photosearch.R
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import com.joshrojas.photosearch.data.remote.response.MediaResponse
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme
import com.joshrojas.photosearch.view.util.VoiceToTextParser
import com.joshrojas.photosearch.view.util.format

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    voiceToTextParser: VoiceToTextParser,
    modifier: Modifier = Modifier
) {
    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::isVoiceSearchEnabled
    )

    LaunchedEffect(key1 = recordAudioLauncher, block = {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    })

    Column(modifier = modifier.padding(20.dp)) {
        Search(viewModel, voiceToTextParser, modifier)
        GridTitle(viewModel, modifier)
        PhotoGrid(viewModel, modifier)
    }
}

@Composable
fun Search(
    viewModel: HomeViewModel,
    voiceToTextParser: VoiceToTextParser,
    modifier: Modifier = Modifier
) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val parserState by voiceToTextParser.state.collectAsStateWithLifecycle()
    val isVoiceEnabled by viewModel.voiceSearchEnabled.collectAsStateWithLifecycle()

    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(parserState.spokenText) {
        if (parserState.isSpeaking.not() && parserState.hasStarted) {
            voiceToTextParser.stop()
            viewModel.updateSearchQuery(parserState.spokenText)
        }
    }

    with(searchState) {
        Search(
            isSearching = isSearchBoxShown,
            isListening = parserState.isSpeaking,
            searchQuery = searchQuery,
            searchFieldFocusRequester = searchFocusRequester,
            searchButtonClick = {
                if (isSearchBoxShown.not()) {
                    viewModel.startSearch()
                } else if (isVoiceEnabled) {
                    voiceToTextParser.start()
                } else {
                    searchFocusRequester.requestFocus()
                }
            },
            updateSearchQuery = viewModel::updateSearchQuery,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Search(
    isSearching: Boolean,
    isListening: Boolean,
    searchQuery: String,
    searchFieldFocusRequester: FocusRequester,
    searchButtonClick: () -> Unit,
    updateSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchButtonFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { searchButtonFocusRequester.requestFocus() }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = modifier
                .padding(start = 10.dp)
                .background(Color.Red, CircleShape)
                .focusRequester(searchButtonFocusRequester),
            onClick = searchButtonClick
        ) {
            if (isSearching.not()) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "search icon"
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.icon_mic),
                    contentDescription = "search icon",
                    tint = if (isListening) {
                        Color.Red
                    } else {
                        LocalContentColor.current
                    }
                )
            }
        }

        if (isSearching) {
            BasicTextField(
                value = searchQuery,
                onValueChange = updateSearchQuery,
                modifier = modifier.focusRequester(searchFieldFocusRequester),
                textStyle = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                keyboardActions = KeyboardActions(onSearch = { updateSearchQuery(searchQuery) }),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Search
                ),
                cursorBrush = SolidColor(Color.White),
                singleLine = true,
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .background(Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                ) {
                    it()
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                            modifier = Modifier.graphicsLayer { alpha = 0.6f }
                        )
                    }
                }
            }

            LaunchedEffect(Unit) { searchFieldFocusRequester.requestFocus() }
        }
    }
}

@Composable
fun GridTitle(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val itemState = viewModel.itemsState.collectAsLazyPagingItems()

    GridTitle(
        searchQuery = searchState.searchQuery,
        hasError = homeState.error != null,
        hasResult = itemState.itemCount > 0,
        modifier = modifier
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun GridTitle(
    searchQuery: String,
    hasError: Boolean,
    hasResult: Boolean,
    modifier: Modifier = Modifier
) {
    val title = if (hasError) {
        stringResource(R.string.grid_search_error)
    } else if (searchQuery.isNotEmpty()) {
        if (hasResult) {
            stringResource(R.string.grid_search, searchQuery)
        } else {
            stringResource(R.string.grid_search_empty, searchQuery)
        }
    } else {
        stringResource(R.string.initial_grid_title)
    }

    Text(
        modifier = modifier
            .padding(top = 10.dp, start = 10.dp, bottom = 10.dp, end = 20.dp)
            .fillMaxWidth(),
        text = title,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PhotoGrid(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val itemsState = viewModel.itemsState.collectAsLazyPagingItems()

    if (homeState.isLoading.not()) {
        PhotoGrid(data = itemsState, modifier = modifier)
    } else {
        Text(
            text = stringResource(id = R.string.grid_search_loading),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth()
        )
    }

}

@Composable
fun PhotoGrid(data: LazyPagingItems<ItemResponse>, modifier: Modifier = Modifier) {
    TvLazyVerticalGrid(
        modifier = modifier,
        columns = TvGridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        content = {
            items(
                data.itemCount,
                key = data.itemKey { it.id }
            ) { index ->
                val item = data[index]
                item?.let { PhotoCard(item, modifier) }
            }
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PhotoCard(item: ItemResponse, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .height(160.dp),
        onClick = { }
    ) {
        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = item.media?.m,
                contentDescription = "${item.title} photo",
                placeholder = painterResource(id = R.drawable.empty_placeholder),
                error = painterResource(id = R.drawable.empty_placeholder),
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )
            Column(
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.author?.nickname} | ${item.published?.format()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoCardPreview() {
    PhotoSearchTheme {
        PhotoCard(
            item = ItemResponse(
                1,
                "Photo Title",
                "",
                MediaResponse("https://live.staticflickr.com/65535/53434847242_6e5e13e92c_m.jpg"),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GridTitleInitial() {
    PhotoSearchTheme { GridTitle("", hasError = false, hasResult = true) }
}

@Preview(showBackground = true)
@Composable
fun GridTitleSearchWithResults() {
    PhotoSearchTheme { GridTitle("Garden", hasError = false, hasResult = true) }
}

@Preview(showBackground = true)
@Composable
fun GridTitleSearchWithNoResults() {
    PhotoSearchTheme { GridTitle("Garden", hasError = false, hasResult = false) }
}