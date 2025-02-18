package com.yazantarifi.compose.library.views

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.yazantarifi.compose.library.MarkdownConfig

@Composable
fun MarkdownImageComponentComposable(
    imageUrl: String,
    isEnabled: Boolean,
    onLinkClickListener: (String, Int) -> Unit
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Image Url Here",
        modifier = Modifier.clickable(enabled = isEnabled) {
            onLinkClickListener(
                imageUrl,
                MarkdownConfig.IMAGE_TYPE
            )
        })
}
