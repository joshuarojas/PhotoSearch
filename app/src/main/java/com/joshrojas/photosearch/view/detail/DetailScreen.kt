package com.joshrojas.photosearch.view.detail

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.joshrojas.photosearch.view.component.CroppedImage
import com.joshrojas.photosearch.view.main.HomeViewModel

@Composable
fun DetailScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val index by viewModel.focusedIndexState.collectAsStateWithLifecycle()
    val items = viewModel.itemsState.collectAsLazyPagingItems()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit, block = { focusRequester.requestFocus() })

    Row(modifier = modifier
        .onKeyEvent {
            if (Key.DirectionRight == it.key && it.type == KeyEventType.KeyUp && index.second < items.itemCount - 1) {
                viewModel.updateFocusIndex(index.second.plus(1))
                return@onKeyEvent true
            } else if (Key.DirectionLeft == it.key && it.type == KeyEventType.KeyUp && index.second > 0) {
                viewModel.updateFocusIndex(index.second.minus(1))
                return@onKeyEvent true
            }
            false
        }
        .focusRequester(focusRequester)
        .focusable()
    ) {
        items[index.first]?.isFocused = false
        items[index.second]?.run {
            isFocused = true
            CroppedImage(image = media?.m.orEmpty(), imageCN = "$title Image")
        }
    }
}