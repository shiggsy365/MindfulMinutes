package com.mindfulminutes.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.*

/**
 * SoundscapeEngine manages multiple MediaPlayer instances for ambient audio mixing.
 * Each soundscape streams from a URL and loops continuously.
 * Volume for each active sound can be independently controlled.
 */
class SoundscapeEngine(private val context: Context) {

    private val players = mutableMapOf<String, MediaPlayer>()
    private val volumes = mutableMapOf<String, Float>()
    private var sleepJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Start playing a soundscape from URL. If already playing, does nothing.
     */
    fun play(id: String, url: String, volume: Float = 0.7f) {
        if (players.containsKey(id)) {
            setVolume(id, volume)
            return
        }

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                isLooping = true
                setVolume(volume * 0.5f, volume * 0.5f)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnErrorListener { _, _, _ ->
                    players.remove(id)
                    volumes.remove(id)
                    true
                }
            }
            players[id] = player
            volumes[id] = volume
        } catch (e: Exception) {
            // Silently handle audio errors
        }
    }

    /**
     * Stop a specific soundscape with optional fade-out.
     */
    fun stop(id: String) {
        players[id]?.let { player ->
            try {
                if (player.isPlaying) player.stop()
                player.release()
            } catch (_: Exception) {}
        }
        players.remove(id)
        volumes.remove(id)
    }

    /**
     * Set volume for a specific soundscape (0.0 to 1.0).
     */
    fun setVolume(id: String, volume: Float) {
        volumes[id] = volume
        players[id]?.let {
            try {
                it.setVolume(volume * 0.5f, volume * 0.5f)
            } catch (_: Exception) {}
        }
    }

    /**
     * Check if a soundscape is currently active.
     */
    fun isPlaying(id: String): Boolean = players.containsKey(id)

    /**
     * Get current volume for a soundscape.
     */
    fun getVolume(id: String): Float = volumes[id] ?: 0f

    /**
     * Get all currently playing soundscape IDs.
     */
    fun getActiveSounds(): Set<String> = players.keys.toSet()

    /**
     * Start a sleep timer that will fade out and stop all sounds after the given minutes.
     */
    fun startSleepTimer(minutes: Int, onTick: (Int) -> Unit, onComplete: () -> Unit) {
        cancelSleepTimer()
        sleepJob = scope.launch {
            var remaining = minutes * 60
            while (remaining > 0) {
                delay(1000)
                remaining--
                onTick(remaining)
            }
            // Fade out all sounds
            for (i in 10 downTo 0) {
                val factor = i / 10f
                players.forEach { (id, player) ->
                    val vol = (volumes[id] ?: 0.7f) * factor
                    try { player.setVolume(vol * 0.5f, vol * 0.5f) } catch (_: Exception) {}
                }
                delay(200)
            }
            stopAll()
            onComplete()
        }
    }

    /**
     * Cancel the sleep timer.
     */
    fun cancelSleepTimer() {
        sleepJob?.cancel()
        sleepJob = null
    }

    /**
     * Stop all playing soundscapes and release resources.
     */
    fun stopAll() {
        players.values.forEach { player ->
            try {
                if (player.isPlaying) player.stop()
                player.release()
            } catch (_: Exception) {}
        }
        players.clear()
        volumes.clear()
    }

    /**
     * Release all resources. Call when the engine is no longer needed.
     */
    fun release() {
        cancelSleepTimer()
        stopAll()
        scope.cancel()
    }
}
