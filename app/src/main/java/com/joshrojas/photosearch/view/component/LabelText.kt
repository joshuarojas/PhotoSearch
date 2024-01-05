package com.joshrojas.photosearch.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LabelText(text: String, style: TextStyle, modifier: Modifier= Modifier) {
    Text(
        text = text,
        style = style,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(showBackground = false)
@Composable
fun LabelTextPreview() {
    PhotoSearchTheme {
        LabelText(
            text = "Demo text",
            style = MaterialTheme.typography.labelMedium
        )
    }
}