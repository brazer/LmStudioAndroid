package com.salanevich.data.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.salanevich.domain.model.SpeechState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListeningRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ListeningRepository, LifecycleEventObserver {

    private val recognizerIntent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000)
        }
    }
    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    speechFlow.tryEmit(SpeechState.Start)
                }
                override fun onBeginningOfSpeech() {
                    speechFlow.tryEmit(SpeechState.Speaking)
                }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    speechFlow.tryEmit(SpeechState.End)
                    stopListening()
                }
                override fun onError(error: Int) {
                    Log.d("ListeningRepositoryImpl", "onError: $error")
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        speechFlow.tryEmit(SpeechState.Text(matches[0]))
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }
    private var speechFlow: MutableStateFlow<SpeechState> = MutableStateFlow(SpeechState.End)

    override fun listen(): Flow<SpeechState> {
        speechFlow = MutableStateFlow(SpeechState.Initialization)
        speechRecognizer.startListening(recognizerIntent)
        return speechFlow
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                if (speechFlow.value != SpeechState.End) {
                    speechRecognizer.stopListening()
                    speechFlow.tryEmit(SpeechState.End)
                }
            }
            else -> {}
        }
    }

}