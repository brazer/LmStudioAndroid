package com.yazantarifi.compose.library.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownCodeComponentComposable(text: String, backgroundColor: Color, textColor: Color) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
        ) {
            Box(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = text.replace("`", "").trim(),
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    fontSize = TextUnit(13f, TextUnitType.Sp),
                    color = textColor
                )
            }
        }
    }
}