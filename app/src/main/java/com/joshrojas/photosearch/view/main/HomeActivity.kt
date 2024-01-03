package com.joshrojas.photosearch.view.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.joshrojas.photosearch.R
import com.joshrojas.photosearch.data.remote.RemoteBuilder
import com.joshrojas.photosearch.data.remote.response.ItemResponse
import com.joshrojas.photosearch.data.remote.response.MediaResponse
import com.joshrojas.photosearch.data.repository.FlickrRepository
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = FlickrRepository(RemoteBuilder.createFilckrAPI())
        val viewModel by viewModels<HomeViewModel> { HomeViewModelFactory(repository) }

        setContent {
            PhotoSearchTheme {
                Surface(
                    modifier = Modifier
                        .background(color = Color.Green)
                        .fillMaxSize(),
                    shape = RectangleShape
                ) {
                    MainScreen(viewModel)
                }
            }
        }

        viewModel.fetchFeed()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(20.dp)) {
        Search(viewModel, modifier)
        GridTitle(viewModel, modifier)
        PhotoGrid(viewModel, modifier)
    }
}

@Composable
fun Search(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val searchState = viewModel.searchState.collectAsStateWithLifecycle()

    val searchFocusRequester = remember { FocusRequester() }

    with(searchState.value) {
        Search(
            isSearching = isSearching,
            searchQuery = searchQuery,
            searchFocusRequester = searchFocusRequester,
            updateSearchState = {
                if (isSearching.not()) {
                    viewModel.startSearch()
                } else {
                    searchFocusRequester.requestFocus()
                }
            },
            updateSearchQuery = {
                viewModel.updateSearchQuery(it)
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Search(
    isSearching: Boolean,
    searchQuery: String,
    searchFocusRequester: FocusRequester,
    updateSearchState: () -> Unit,
    updateSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchIconFocusRequester = remember { FocusRequester() }

    Row {
        IconButton(
            modifier = modifier
                .padding(start = 10.dp)
                .background(Color.Cyan, CircleShape)
                .focusRequester(searchIconFocusRequester),
            onClick = {
                updateSearchState()
            }
        ) {
            Icon(Icons.Filled.Search, contentDescription = "search icon")
        }

        LaunchedEffect(Unit) {
            searchIconFocusRequester.requestFocus()
        }

        if (isSearching) {
            BasicTextField(
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .focusRequester(searchFocusRequester),
                value = searchQuery,
                singleLine = true,
                onValueChange = updateSearchQuery,
                cursorBrush = SolidColor(Color.White),
                textStyle = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    updateSearchQuery(searchQuery)
                }),
            ) {
                Box(modifier = Modifier.padding(start = 20.dp)) {
                    it()
                    if (searchQuery.isEmpty()) {
                        Text(
                            modifier = Modifier.graphicsLayer { alpha = 0.6f },
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                        )
                    }
                }
            }

            LaunchedEffect(Unit) { searchFocusRequester.requestFocus() }
        }
    }
}

@Composable
fun GridTitle(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val searchState = viewModel.searchState.collectAsStateWithLifecycle()
    val homeState = viewModel.homeState.collectAsStateWithLifecycle()

    with(homeState.value) {
        GridTitle(
            searchQuery = searchState.value.searchQuery,
            hasError = error != null,
            data.isNotEmpty(),
            modifier
        )
    }
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
        stringResource(id = R.string.grid_search_error)
    } else if (searchQuery.isNotEmpty()) {
        if (hasResult) {
            stringResource(id = R.string.grid_search, searchQuery)
        } else {
            stringResource(id = R.string.grid_search_empty, searchQuery)
        }
    } else {
        stringResource(id = R.string.initial_grid_title)
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
    val result = viewModel.homeState.collectAsStateWithLifecycle()

    with(result.value) {
        if (isLoading.not()) {
            PhotoGrid(data = data, modifier = modifier)
        } else {
            Text(
                text = stringResource(id = R.string.grid_search_loading),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PhotoGrid(data: List<ItemResponse>, modifier: Modifier = Modifier) {
    TvLazyVerticalGrid(
        modifier = modifier,
        columns = TvGridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        content = {
            items(data) {
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .height(160.dp),
                    onClick = { }
                ) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(Color.Cyan)
                    ) {
                        AsyncImage(
                            model = it.media?.m,
                            contentDescription = "${it.title} photo",
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
                                text = it.title.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${it.author?.nickname} | ${it.dateTaken}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PhotoGridPreview() {
    PhotoSearchTheme {
        PhotoGrid(
            data = listOf(
                ItemResponse(
                    "Photo Title",
                    "",
                    MediaResponse("https://live.staticflickr.com/65535/53434847242_6e5e13e92c_m.jpg"),
                )
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