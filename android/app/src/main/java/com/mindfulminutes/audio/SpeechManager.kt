package com.mindfulminutes.audio

import android.content.Context

import android.media.MediaPlayer
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import java.net.URLEncoder
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false
    private var mediaPlayer: MediaPlayer? = null
    
    var serverUrl: String? = null
    var isMuted: Boolean = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("SpeechManager", "Language not supported")
            } else {
                isInitialized = true
                
                // Calm, zen parameters
                tts?.setSpeechRate(0.85f)
                tts?.setPitch(0.95f)

                // Try to find a high-quality neural voice
                val availableVoices = tts?.voices ?: emptySet()
                val bestVoice = availableVoices.find { voice ->
                    val name = voice.name.lowercase()
                    (name.contains("neural") || name.contains("studio") || name.contains("natural")) && 
                    !voice.isNetworkConnectionRequired
                } ?: availableVoices.find { it.name.contains("en-us-x-sfg#female_2-local") }
                
                bestVoice?.let {
                    tts?.setVoice(it)
                    Log.d("SpeechManager", "Selected voice: ${it.name}")
                }
            }
        } else {
            Log.e("SpeechManager", "TTS Initialization failed")
        }
    }

    fun speak(text: String) {
        if (isMuted) return
        // 1. Try bundled resource first
        val resId = AudioMap.map[text]
        if (resId != null) {
            speakResource(resId)
            return
        }

        // 2. Try remote server
        val url = serverUrl
        if (!url.isNullOrBlank()) {
            speakRemote(url, text)
        } else if (isInitialized) {
            // 3. Fallback to local TTS
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun speakResource(resId: Int) {
        try {
            stop()
            mediaPlayer = MediaPlayer.create(context, resId).apply {
                setOnCompletionListener { 
                    release()
                    mediaPlayer = null
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("SpeechManager", "Resource playback failed", e)
        }
    }

    private fun speakRemote(baseUrl: String, text: String) {
        try {
            stop() // Stop any current playback
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val fullUrl = if (baseUrl.endsWith("/")) "${baseUrl}tts?text=$encodedText" else "$baseUrl/tts?text=$encodedText"
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(fullUrl))
                setOnPreparedListener { start() }
                setOnCompletionListener { 
                    release()
                    mediaPlayer = null
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("SpeechManager", "Remote TTS failed, falling back to local", e)
            if (isInitialized) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    fun stop() {
        tts?.stop()
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun shutdown() {
        tts?.shutdown()
        stop()
    }
}
