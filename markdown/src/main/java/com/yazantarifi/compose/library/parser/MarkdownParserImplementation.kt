package com.yazantarifi.compose.library.parser

import com.yazantarifi.compose.library.MarkdownConfig
import com.yazantarifi.compose.library.components.MarkdownComponent

interface MarkdownParserImplementation {

    fun setMarkdownConfig(config: MarkdownConfig): MarkdownParser

    fun setMarkdownContent(content: String): MarkdownParser

    fun build(): ArrayList<MarkdownComponent>

}