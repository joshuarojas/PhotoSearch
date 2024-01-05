package com.joshrojas.photosearch.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.joshrojas.photosearch.R
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme

@Composable
fun SearchField(
    style: TextStyle,
    searchQuery: String,
    searchUpdate: (String) -> Unit,
    searchFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = searchQuery,
        textStyle = style,
        onValueChange = searchUpdate,
        modifier = modifier.focusRequester(searchFocusRequester),
        keyboardActions = KeyboardActions(onSearch = { searchUpdate(searchQuery) }),
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
                .padding(10.dp)
        ) {
            it()
            if (searchQuery.isEmpty()) {
                TitleText(
                    text = stringResource(R.string.search_hint),
                    style = style,
                    modifier = modifier.graphicsLayer { alpha = 0.6f }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchFieldPreview() {
    PhotoSearchTheme {
        SearchField(
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
            searchQuery = "Example query",
            searchUpdate = {},
            searchFocusRequester = FocusRequester(),
        )
    }
}