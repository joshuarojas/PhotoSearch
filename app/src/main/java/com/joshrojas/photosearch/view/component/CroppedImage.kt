package com.joshrojas.photosearch.view.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.joshrojas.photosearch.R
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme

@Composable
fun CroppedImage(image: String, imageCN: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = image,
        contentDescription = imageCN,
        placeholder = painterResource(id = R.drawable.empty_placeholder),
        error = painterResource(id = R.drawable.empty_placeholder),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    )
}

@Preview(showBackground = true)
@Composable
fun CroppedImagePreview() {
    PhotoSearchTheme {
        CroppedImage(
            image = "https://live.staticflickr.com/65535/53442785954_484869c5e9_m.jpg",
            imageCN = "demo image cn"
        )
    }
}