package com.yazantarifi.compose.library.views

import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MarkdownShieldComponentComposable(content: String) {
    val context = LocalContext.current
    AndroidView(factory = {
        WebView(context).apply {
            this.loadUrl(content)
        }
    }, modifier = Modifier.wrapContentWidth().wrapContentHeight().padding(5.dp))
}