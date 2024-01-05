package com.joshrojas.photosearch.view.util

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VoiceToTextParserTest {

    @Mock
    private lateinit var appContext: Context

    private lateinit var voiceToTextParser: VoiceToTextParser
    private lateinit var mockedSpeechRecognizer: MockedStatic<SpeechRecognizer>

    @Before
    fun setUp() {
        mockedSpeechRecognizer = Mockito.mockStatic(SpeechRecognizer::class.java)
        mockedSpeechRecognizer
            .`when`<SpeechRecognizer> { SpeechRecognizer.createSpeechRecognizer(appContext) }
            .then { Mockito.mock<SpeechRecognizer>() }

        voiceToTextParser = VoiceToTextParser(appContext)
    }

    @After
    fun tearDown() {
        mockedSpeechRecognizer.close()
    }

    @Test
    fun voiceToTextParser_StartListening() {
        // given
        mockedSpeechRecognizer
            .`when`<SpeechRecognizer> { SpeechRecognizer.isRecognitionAvailable(appContext) }
            .then { true }

        // when
        voiceToTextParser.start()

        // then
        assert(voiceToTextParser.state.value.hasStarted)
        assert(voiceToTextParser.state.value.isSpeaking)
    }

    @Test
    fun voiceToTextParser_StartListening_RecognitionNotAvailable() {
        // given
        mockedSpeechRecognizer
            .`when`<SpeechRecognizer> { SpeechRecognizer.isRecognitionAvailable(appContext) }
            .then { false }

        // when
        voiceToTextParser.start()

        // then
        assert(voiceToTextParser.state.value.hasStarted)
        assert(voiceToTextParser.state.value.error?.isNotEmpty() ?: false)
    }

    @Test
    fun voiceToTextParser_StopListening() {
        // given
        val emptyVoiceToTExtValue = ""

        // when
        voiceToTextParser.start()
        voiceToTextParser.stop()

        // then
        assert(voiceToTextParser.state.value.isSpeaking.not())
        assert(voiceToTextParser.state.value.hasStarted.not())
        assertEquals(emptyVoiceToTExtValue, voiceToTextParser.state.value.spokenText)
    }

    @Test
    fun voiceToTextParser_ParseVoiceToText() {
        // given
        val voiceToTextValue = "voice to text"
        val result = Mockito.mock(Bundle::class.java)
        Mockito.`when`(result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))
            .thenReturn(arrayListOf("voice to text"))

        // when
        voiceToTextParser.start()
        voiceToTextParser.onResults(result)

        // then
        assert(voiceToTextParser.state.value.isSpeaking.not())
        assertEquals(voiceToTextValue, voiceToTextParser.state.value.spokenText)
    }
}