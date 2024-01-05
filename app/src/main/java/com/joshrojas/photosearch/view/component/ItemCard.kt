package com.joshrojas.photosearch.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .height(160.dp),
        onClick = { }
    ) {
        Box(modifier = modifier.fillMaxWidth(), content = content)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemCardPreview() {
    ItemCard {
        CroppedImage(
            image = "https://live.staticflickr.com/65535/53442785954_484869c5e9_m.jpg",
            imageCN = "demo image cn"
        )
        TitleText(
            text = "this is a demo",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
        )
    }
}