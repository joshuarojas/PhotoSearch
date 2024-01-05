package com.joshrojas.photosearch.view.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.joshrojas.photosearch.R
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import com.joshrojas.photosearch.view.component.CroppedImage
import com.joshrojas.photosearch.view.component.ItemCard
import com.joshrojas.photosearch.view.component.LabelText
import com.joshrojas.photosearch.view.component.SearchButton
import com.joshrojas.photosearch.view.component.SearchField
import com.joshrojas.photosearch.view.component.TitleText
import com.joshrojas.photosearch.view.component.VerticalAlbum
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
        SearchHeader(viewModel, voiceToTextParser, modifier)
        GridTitle(viewModel, modifier)
        PhotoGrid(viewModel, modifier)
    }
}

@Composable
fun SearchHeader(
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
        SearchHeader(
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
fun SearchHeader(
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

    Row(verticalAlignment = Alignment.CenterVertically) {
        SearchButton(
            isSearching = isSearching,
            isListening = isListening,
            onClick = searchButtonClick,
            buttonFocusRequester = searchButtonFocusRequester,
            modifier = modifier
        )

        if (isSearching) {
            SearchField(
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                searchQuery = searchQuery,
                searchUpdate = updateSearchQuery,
                searchFocusRequester = searchFieldFocusRequester,
                modifier = modifier
            )
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

    TitleText(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier.padding(10.dp)
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
    VerticalAlbum(modifier = modifier) {
        items(
            data.itemCount,
            key = data.itemKey { it.id }
        ) { index ->
            val item = data[index]
            item?.let { PhotoCard(item, modifier) }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PhotoCard(item: ItemResponse, modifier: Modifier = Modifier) {
    ItemCard(modifier = modifier) {
        CroppedImage(
            image = item.media?.m.orEmpty(),
            imageCN = "${item.title} photo",
            modifier = modifier
        )
        Column(
            modifier = modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )
                .padding(8.dp)
        ) {
            LabelText(
                text = item.title.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                modifier = modifier
            )
            LabelText(
                text = "${item.author?.nickname} | ${item.published?.format()}",
                style = MaterialTheme.typography.labelSmall,
                modifier = modifier
            )
        }
    }
}