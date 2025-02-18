package com.yazantarifi.compose.library.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownItalicTextComponentComposable(text: String, color: Color, backgroundColor: Color) {
    Text(
        text = text,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(5.dp).background(backgroundColor),
        color = color
    )
}