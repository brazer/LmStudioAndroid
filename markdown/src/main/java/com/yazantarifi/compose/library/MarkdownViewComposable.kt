package com.yazantarifi.compose.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yazantarifi.compose.library.components.*
import com.yazantarifi.compose.library.parser.MarkdownParser
import com.yazantarifi.compose.library.views.*

@Composable
fun MarkdownViewComposable(
    modifier: Modifier,
    content: String,
    config: MarkdownConfig,
    onLinkClickListener: (link: String, type: Int) -> Unit = { _, _ -> },
) {
    val parser = MarkdownParser()
        .setMarkdownConfig(config)
        .setMarkdownContent(content)
        .build()

    Box(modifier = modifier) {
        val isScrollEnabled = config.isScrollEnabled
        if (isScrollEnabled) {
            LazyColumn {
                items(parser) { item ->
                    RenderComponent(item, config, onLinkClickListener)
                }
            }
        } else {
            Column {
                parser.forEach {
                    RenderComponent(it, config, onLinkClickListener)
                }
            }
        }
    }
}

@Composable
private fun RenderComponent(item: MarkdownComponent, config: MarkdownConfig, onLinkClickListener: (String, Int) -> Unit) {
    when (item) {
        is MarkdownCodeComponent -> MarkdownCodeComponentComposable(
            text = item.codeBlock,
            backgroundColor = config.colors?.get(MarkdownConfig.CODE_BACKGROUND_COLOR) ?: Color.Transparent,
            textColor = config.colors?.get(MarkdownConfig.CODE_BLOCK_TEXT_COLOR) ?: Color.White
        )
        is MarkdownItalicTextComponent -> MarkdownItalicTextComponentComposable(
            text = item.text,
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
            color = config.colors?.get(MarkdownConfig.TEXT_COLOR) ?: Color.Black
        )
        is MarkdownBoldTextComponent -> MarkdownBoldTextComponentComposable(
            text = item.text,
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
            color = config.colors?.get(MarkdownConfig.TEXT_COLOR) ?: Color.Black
        )
        is MarkdownLinkComponent -> MarkdownLinkComponentComposable(
            text = item.text,
            link = item.link,
            color = config.colors?.get(MarkdownConfig.LINKS_COLOR) ?: Color.Black,
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
            isLinksClickable = config.isLinksClickable,
            onLinkClickListener = onLinkClickListener
        )
        is MarkdownCheckBoxComponent -> MarkdownCheckBoxComponentComposable(
            text = item.text,
            color = config.colors?.get(MarkdownConfig.CHECKBOX_COLOR) ?: Color.Black,
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
            isChecked = item.isChecked
        )
        is MarkdownShieldComponent -> MarkdownShieldComponentComposable(content = item.url)
        is MarkdownTextComponent -> MarkdownTextComponentComposable(
            text = item.text,
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
            color = config.colors?.get(MarkdownConfig.TEXT_COLOR) ?: Color.Black
        )
        is MarkdownSpaceComponent -> MarkDownSpaceComponentComposable(
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
        )
        is MarkdownImageComponent -> MarkdownImageComponentComposable(
            imageUrl = item.image,
            isEnabled = config.isImagesClickable,
            onLinkClickListener = onLinkClickListener
        )
        is MarkdownStyledTextComponent -> MarkdownStyledTextComponentComposable(
            text = item.text,
            layer = item.layer,
            backgroundColor = config.colors?.get(MarkdownConfig.COMPONENT_BACKGROUND_COLOR) ?: Color.Transparent,
            color = config.colors?.get(MarkdownConfig.HASH_TEXT_COLOR) ?: Color.Black
        )
    }
}
