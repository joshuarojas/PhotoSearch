package com.joshrojas.photosearch.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyGridScope
import androidx.tv.foundation.lazy.grid.TvLazyGridState
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme

@Composable
fun VerticalAlbum(
    state: TvLazyGridState,
    modifier: Modifier = Modifier,
    content: TvLazyGridScope.() -> Unit
) {
    TvLazyVerticalGrid(
        state = state,
        modifier = modifier,
        columns = TvGridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun VerticalAlbumPreview() {
    PhotoSearchTheme {
        VerticalAlbum(rememberTvLazyGridState()) {
            items(6) {
                ItemCard {}
            }
        }
    }
}