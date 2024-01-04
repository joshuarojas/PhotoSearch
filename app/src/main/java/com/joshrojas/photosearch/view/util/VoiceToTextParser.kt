package com.joshrojas.photosearch.view.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VoiceToTextParser(private val context: Context) : RecognitionListener {

    private val _state = MutableStateFlow(VoiceToTextParserState())
    val state = _state.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun start(languageCode: String = "en") {
        _state.update { VoiceToTextParserState(hasStarted = true) }

        if (SpeechRecognizer.isRecognitionAvailable(context).not()) {
            _state.update { it.copy(error = "Recognition not available") }
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            }

            recognizer.setRecognitionListener(this)
            recognizer.startListening(intent)

            _state.update { it.copy(isSpeaking = true) }
        }
    }

    fun stop() {
        recognizer.stopListening()
        _state.update { VoiceToTextParserState(isSpeaking = false, hasStarted = false) }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _state.update { it.copy(error = null) }
    }

    override fun onBeginningOfSpeech() {
        // empty
    }

    override fun onRmsChanged(rmsdB: Float) {
        // empty
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // empty
    }

    override fun onEndOfSpeech() {
        _state.update { it.copy(isSpeaking = false) }
    }

    override fun onError(error: Int) {
        if (error != SpeechRecognizer.ERROR_CLIENT) {
            _state.update { it.copy(error = "Error: $error") }
        }
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
            ?.let { result ->
                _state.update { it.copy(spokenText = result) }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // empty
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // empty
    }
}

data class VoiceToTextParserState(
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val hasStarted: Boolean = false,
    val error: String? = null
)