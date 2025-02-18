package com.yazantarifi.compose.library.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yazantarifi.compose.library.MarkdownConfig

@Composable
fun MarkdownLinkComponentComposable(
    text: String,
    link: String,
    color: Color,
    backgroundColor: Color,
    isLinksClickable: Boolean,
    onLinkClickListener: (String, Int) -> Unit
) {
    Text(
        text = text,
        color = color,
        modifier = Modifier
            .padding(5.dp)
            .background(backgroundColor)
            .clickable(enabled = isLinksClickable) {
                onLinkClickListener(
                    link,
                    MarkdownConfig.LINK_TYPE
                )
            })
}
