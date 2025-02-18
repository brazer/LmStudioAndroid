package com.yazantarifi.compose.library

import androidx.compose.ui.graphics.Color

data class MarkdownConfig(
    val isLinksClickable: Boolean = false,
    val isImagesClickable: Boolean = false,
    val colors: HashMap<String, Color>? = null,
    val isScrollEnabled: Boolean = true
) {
    companion object {
        const val TEXT_COLOR = "text"
        const val LINKS_COLOR = "links"
        const val CODE_BACKGROUND_COLOR = "code_background"
        const val CODE_BLOCK_TEXT_COLOR = "code_text"
        const val HASH_TEXT_COLOR = "hashes"
        const val CHECKBOX_COLOR = "checkbox"
        const val COMPONENT_BACKGROUND_COLOR = "background"

        const val IMAGE_TYPE = 1
        const val LINK_TYPE = 2
    }
}
