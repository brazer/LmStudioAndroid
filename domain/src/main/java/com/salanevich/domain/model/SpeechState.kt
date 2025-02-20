package com.salanevich.domain.model

sealed class SpeechState {
    data object Start : SpeechState()
    data object End : SpeechState()
    data class Text(val value: String) : SpeechState()
}