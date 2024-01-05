package com.joshrojas.photosearch.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.LocalContentColor
import com.joshrojas.photosearch.R
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchButton(
    isSearching: Boolean,
    isListening: Boolean,
    onClick: () -> Unit,
    buttonFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier
            .padding(start = 10.dp)
            .background(Color.Red, CircleShape)
            .focusRequester(buttonFocusRequester),
        onClick = onClick
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
}

@Preview(showBackground = true)
@Composable
fun SearchButtonPreview() {
    PhotoSearchTheme {
        SearchButton(
            isSearching = false,
            isListening = false,
            onClick = {},
            buttonFocusRequester = FocusRequester()
        )
    }
}