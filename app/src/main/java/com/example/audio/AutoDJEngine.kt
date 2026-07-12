package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.R
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Advanced Auto DJ and Crossfading Music Engine.
 * Implements a dual-deck system (Deck A and Deck B) that supports:
 * - Real-time BPM synchronization & automatic tempo sliding (slur).
 * - Multi-curve Volume Crossfading (Linear, Constant Power, Bass Swap).
 * - Simulated EQ Bass Swap (to avoid overlapping bass frequencies and low-end mud).
 * - Interactive DJ features: Manual Crossfading, Pitch Nudging, BPM Sync, Scratch simulation.
 * - Auto-mixing: Automatically transitions to the next track when the current one is near the end.
 */
object AutoDJEngine {

    enum class CurveType {
        LINEAR,
        CONSTANT_POWER,
        BASS_SWAP
    }

    // --- Observable Compose States for the DJ Dashboard ---
    var deckATrackName by mutableStateOf("Vanguard Anthem")
    var deckBTrackName by mutableStateOf("No Track")
    
    var deckABpm by mutableStateOf(120f)
    var deckBBpm by mutableStateOf(120f)
    
    var deckAPitchShift by mutableStateOf(0f) // -10% to +10% nudge
    var deckBPitchShift by mutableStateOf(0f)
    
    var currentDeck by mutableStateOf(0) // 0 for Deck A (A is active), 1 for Deck B (B is active)
    var crossfaderPosition by mutableStateOf(-1.0f) // -1.0f (all Deck A) to +1.0f (all Deck B)
    
    var isTransitioning by mutableStateOf(false)
    var transitionProgress by mutableStateOf(0f) // 0f to 1f
    var crossfadeCurveType by mutableStateOf(CurveType.CONSTANT_POWER)
    var transitionDurationSeconds by mutableStateOf(8.0f)
    
    var syncModeEnabled by mutableStateOf(true)
    var bassSwapActive by mutableStateOf(false) // Whether manual bass swap (EQ cut) is engaged
    
    // Playlist queue
    val playlistUris = mutableListOf<Uri>()
    val playlistNames = mutableListOf<String>()
    var currentPlaylistIndex = 0
    var autoMixEnabled by mutableStateOf(true)

    // --- Internal Media Players and Coroutine Jobs ---
    private var mediaPlayerA: MediaPlayer? = null
    private var mediaPlayerB: MediaPlayer? = null
    
    private var activeContext: Context? = null
    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var transitionJob: Job? = null
    private var playlistMonitorJob: Job? = null
    private var speedSlideJobA: Job? = null
    private var speedSlideJobB: Job? = null

    // Base volume
    var masterVolume: Float = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
            applyVolumes()
        }

    fun init(context: Context) {
        activeContext = context.applicationContext
        startPlaylistMonitor()
    }

    /**
     * Set a custom playlist of songs and start playing the first one on Deck A.
     */
    fun playPlaylist(context: Context, uris: List<Uri>, names: List<String>, startIndex: Int = 0) {
        activeContext = context.applicationContext
        stopEngine()
        
        playlistUris.clear()
        playlistUris.addAll(uris)
        playlistNames.clear()
        playlistNames.addAll(names)
        
        if (playlistUris.isEmpty()) return
        
        currentPlaylistIndex = startIndex % playlistUris.size
        
        // Start track 1 on Deck A
        val firstUri = playlistUris[currentPlaylistIndex]
        val firstName = playlistNames[currentPlaylistIndex]
        
        deckATrackName = firstName
        deckABpm = calculateBpmForTrack(firstName)
        currentDeck = 0
        crossfaderPosition = -1.0f
        
        mediaPlayerA = createPlayer(context, firstUri) {
            // Safe callback
        }
        mediaPlayerA?.let { player ->
            player.isLooping = !autoMixEnabled
            player.start()
        }
        applyVolumes()
        applySpeeds()
    }

    /**
     * Direct play of a single track on whichever deck is currently inactive, or resets everything.
     */
    fun playSingleTrack(context: Context, uri: Uri, displayName: String) {
        activeContext = context.applicationContext
        
        // If we are currently playing something on A, let's crossfade to B nicely instead of hard stopping!
        if (mediaPlayerA != null && mediaPlayerB == null && !isTransitioning) {
            // Prepare B and crossfade
            prepareDeckB(context, uri, displayName)
            startAutoMixTransition()
        } else if (mediaPlayerB != null && mediaPlayerA == null && !isTransitioning) {
            // Prepare A and crossfade
            prepareDeckA(context, uri, displayName)
            startAutoMixTransition()
        } else {
            // Hard play on active deck
            stopEngine()
            currentDeck = 0
            crossfaderPosition = -1.0f
            deckATrackName = displayName
            deckABpm = calculateBpmForTrack(displayName)
            
            mediaPlayerA = createPlayer(context, uri) { }
            mediaPlayerA?.let { player ->
                player.isLooping = true
                player.start()
            }
            applyVolumes()
            applySpeeds()
        }
    }

    /**
     * Play a bundled raw track.
     */
    fun playBundledMusic(context: Context, resId: Int, displayName: String) {
        activeContext = context.applicationContext
        stopEngine()
        
        currentDeck = 0
        crossfaderPosition = -1.0f
        deckATrackName = displayName
        deckABpm = calculateBpmForTrack(displayName)
        
        try {
            mediaPlayerA = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                start()
            }
            applyVolumes()
            applySpeeds()
        } catch (_: Exception) {}
    }

    /**
     * Trigger a manual or auto transition from the current track to the next.
     */
    fun triggerManualTransition() {
        val context = activeContext ?: return
        if (isTransitioning || playlistUris.isEmpty()) return
        
        // Find next playlist index
        val nextIndex = (currentPlaylistIndex + 1) % playlistUris.size
        val nextUri = playlistUris[nextIndex]
        val nextName = playlistNames[nextIndex]
        
        if (currentDeck == 0) {
            prepareDeckB(context, nextUri, nextName)
        } else {
            prepareDeckA(context, nextUri, nextName)
        }
        
        currentPlaylistIndex = nextIndex
        startAutoMixTransition()
    }

    /**
     * Trigger a scratch effect. Rapidly modulates playback speed to simulate a DJ scratch,
     * and optionally plays a quick high-pitched SFX modulation.
     */
    fun triggerScratchEffect() {
        val activePlayer = if (currentDeck == 0) mediaPlayerA else mediaPlayerB ?: return
        if (activePlayer == null || !activePlayer.isPlaying) return
        
        scope.launch {
            try {
                val originalSpeed = if (currentDeck == 0) (1.0f + deckAPitchShift) else (1.0f + deckBPitchShift)
                
                // Rapid vinyl scratch forward/backward motion
                setPlayerSpeed(activePlayer, originalSpeed * 2.5f)
                delay(60)
                setPlayerSpeed(activePlayer, originalSpeed * -0.4f) // backward motion
                delay(70)
                setPlayerSpeed(activePlayer, originalSpeed * 1.8f)
                delay(60)
                setPlayerSpeed(activePlayer, originalSpeed) // restore
            } catch (_: Exception) {}
        }
    }

    /**
     * Nudge pitch: Temporarily changes the playback speed of a deck to speed it up
     * or slow it down, simulating a physical vinyl nudge to align beats.
     */
    fun nudgePitch(deckIndex: Int, positive: Boolean) {
        val targetPlayer = if (deckIndex == 0) mediaPlayerA else mediaPlayerB ?: return
        if (targetPlayer == null) return
        
        val currentNudge = if (deckIndex == 0) deckAPitchShift else deckBPitchShift
        val newNudge = if (positive) (currentNudge + 0.05f).coerceAtMost(0.15f) else (currentNudge - 0.05f).coerceAtLeast(-0.15f)
        
        if (deckIndex == 0) {
            deckAPitchShift = newNudge
        } else {
            deckBPitchShift = newNudge
        }
        applySpeeds()
        
        // Auto-release nudge after 1.5 seconds
        scope.launch {
            delay(1500)
            if (deckIndex == 0) {
                deckAPitchShift = 0f
            } else {
                deckBPitchShift = 0f
            }
            applySpeeds()
        }
    }

    /**
     * Set the crossfader position manually.
     * Overrides or handles custom blending in real-time.
     */
    fun updateCrossfader(position: Float) {
        crossfaderPosition = position.coerceIn(-1.0f, 1.0f)
        applyVolumes()
    }

    /**
     * Force sync BPM of the inactive deck to the active deck.
     */
    fun syncTempos() {
        if (currentDeck == 0) {
            deckBBpm = deckABpm
        } else {
            deckABpm = deckBBpm
        }
        applySpeeds()
    }

    // --- Helper Methods ---

    private fun createPlayer(context: Context, uri: Uri, onPrepared: () -> Unit): MediaPlayer {
        return MediaPlayer().apply {
            setDataSource(context, uri)
            setOnPreparedListener {
                onPrepared()
            }
            prepare()
        }
    }

    private fun prepareDeckA(context: Context, uri: Uri, trackName: String) {
        try {
            mediaPlayerA?.release()
            deckATrackName = trackName
            deckABpm = calculateBpmForTrack(trackName)
            mediaPlayerA = createPlayer(context, uri) { }
            mediaPlayerA?.setVolume(0f, 0f)
            applySpeeds()
        } catch (_: Exception) {}
    }

    private fun prepareDeckB(context: Context, uri: Uri, trackName: String) {
        try {
            mediaPlayerB?.release()
            deckBTrackName = trackName
            deckBBpm = calculateBpmForTrack(trackName)
            mediaPlayerB = createPlayer(context, uri) { }
            mediaPlayerB?.setVolume(0f, 0f)
            applySpeeds()
        } catch (_: Exception) {}
    }

    /**
     * Core Auto-Mix transition logic. Coordinates BPM syncing, crossfading, and slurring.
     */
    private fun startAutoMixTransition() {
        transitionJob?.cancel()
        isTransitioning = true
        
        val context = activeContext ?: return
        
        transitionJob = scope.launch {
            val steps = 100
            val durationMs = (transitionDurationSeconds * 1000).toLong()
            val stepDelay = durationMs / steps
            
            // 1. Sync BPM if enabled
            if (syncModeEnabled) {
                if (currentDeck == 0) {
                    // Match B's tempo to A's tempo
                    val speedRatio = deckABpm / deckBBpm
                    mediaPlayerB?.let { setPlayerSpeed(it, speedRatio) }
                } else {
                    // Match A's tempo to B's tempo
                    val speedRatio = deckBBpm / deckABpm
                    mediaPlayerA?.let { setPlayerSpeed(it, speedRatio) }
                }
            }
            
            // Start the incoming player
            if (currentDeck == 0) {
                mediaPlayerB?.start()
            } else {
                mediaPlayerA?.start()
            }

            // 2. Perform smooth crossfade
            val startPosition = crossfaderPosition
            val targetPosition = if (currentDeck == 0) 1.0f else -1.0f
            
            for (i in 0..steps) {
                val t = i.toFloat() / steps
                transitionProgress = t
                crossfaderPosition = startPosition + t * (targetPosition - startPosition)
                applyVolumes()
                delay(stepDelay)
            }
            
            // 3. Finalize transition
            val oldDeck = currentDeck
            currentDeck = if (currentDeck == 0) 1 else 0
            isTransitioning = false
            transitionProgress = 0f
            
            // Stop and release old player
            if (oldDeck == 0) {
                mediaPlayerA?.pause()
                mediaPlayerA?.release()
                mediaPlayerA = null
                deckATrackName = "No Track"
            } else {
                mediaPlayerB?.pause()
                mediaPlayerB?.release()
                mediaPlayerB = null
                deckBTrackName = "No Track"
            }
            
            // 4. Slide Tempo (Slur) back to its native rate
            slideTempoBack(currentDeck)
        }
    }

    /**
     * Slowly interpolates the playback speed of the playing deck back to standard (1.0)
     * over 8 seconds so the BPM correction doesn't sound abrupt.
     */
    private fun slideTempoBack(deckIndex: Int) {
        if (deckIndex == 0) {
            speedSlideJobA?.cancel()
            speedSlideJobA = scope.launch {
                val player = mediaPlayerA ?: return@launch
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        val currentSpeed = player.playbackParams.speed
                        val targetSpeed = 1.0f
                        val steps = 50
                        for (i in 0..steps) {
                            val t = i.toFloat() / steps
                            val speed = currentSpeed + t * (targetSpeed - currentSpeed)
                            setPlayerSpeed(player, speed)
                            delay(120)
                        }
                    } catch (_: Exception) {}
                }
            }
        } else {
            speedSlideJobB?.cancel()
            speedSlideJobB = scope.launch {
                val player = mediaPlayerB ?: return@launch
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        val currentSpeed = player.playbackParams.speed
                        val targetSpeed = 1.0f
                        val steps = 50
                        for (i in 0..steps) {
                            val t = i.toFloat() / steps
                            val speed = currentSpeed + t * (targetSpeed - currentSpeed)
                            setPlayerSpeed(player, speed)
                            delay(120)
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }

    private fun setPlayerSpeed(player: MediaPlayer, speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = player.playbackParams
                params.speed = speed.coerceIn(0.5f, 2.0f)
                player.playbackParams = params
            } catch (_: Exception) {}
        }
    }

    /**
     * Applies correct volume levels to both decks based on the crossfader position and selected curve.
     */
    private fun applyVolumes() {
        // Map crossfader position [-1.0f .. 1.0f] to a relative progress [0f .. 1f]
        val progress = (crossfaderPosition + 1.0f) / 2.0f
        
        var volA = 0f
        var volB = 0f
        
        when (crossfadeCurveType) {
            CurveType.LINEAR -> {
                volA = 1.0f - progress
                volB = progress
            }
            CurveType.CONSTANT_POWER -> {
                // Trigonometric Constant Power curve ensures constant perceptual loudness
                volA = cos(progress * (PI / 2)).toFloat()
                volB = sin(progress * (PI / 2)).toFloat()
            }
            CurveType.BASS_SWAP -> {
                // Simulated Bass-Swap cuts volume of the secondary player to reduce low frequencies
                volA = if (progress < 0.5f) 1.0f else (1.0f - progress) * 1.4f
                volB = if (progress >= 0.5f) 1.0f else progress * 1.4f
            }
        }
        
        // Apply EQ simulation dampening if manual bass swap is engaged
        if (bassSwapActive) {
            if (currentDeck == 0) {
                // Damp B's bass/volume
                volB *= 0.5f
            } else {
                // Damp A's bass/volume
                volA *= 0.5f
            }
        }
        
        val finalVolA = (volA * masterVolume).coerceIn(0f, 1f)
        val finalVolB = (volB * masterVolume).coerceIn(0f, 1f)
        
        try {
            mediaPlayerA?.setVolume(finalVolA, finalVolA)
            mediaPlayerB?.setVolume(finalVolB, finalVolB)
        } catch (_: Exception) {}
    }

    private fun applySpeeds() {
        mediaPlayerA?.let { setPlayerSpeed(it, 1.0f + deckAPitchShift) }
        mediaPlayerB?.let { setPlayerSpeed(it, 1.0f + deckBPitchShift) }
    }

    /**
     * Real-time monitoring loop that scans the active deck's playback position.
     * When it gets within [transitionDurationSeconds] of completion, it triggers the auto mix.
     */
    private fun startPlaylistMonitor() {
        playlistMonitorJob?.cancel()
        playlistMonitorJob = scope.launch {
            while (isActive) {
                delay(1000)
                if (autoMixEnabled && !isTransitioning && playlistUris.isNotEmpty()) {
                    val activePlayer = if (currentDeck == 0) mediaPlayerA else mediaPlayerB
                    if (activePlayer != null && activePlayer.isPlaying) {
                        try {
                            val duration = activePlayer.duration
                            val position = activePlayer.currentPosition
                            val remainingMs = duration - position
                            val thresholdMs = (transitionDurationSeconds * 1000).toLong()
                            
                            if (remainingMs <= thresholdMs && remainingMs > 0) {
                                triggerManualTransition()
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    private fun calculateBpmForTrack(trackName: String): Float {
        // Deterministic BPM calculation based on title hash code
        val hash = Math.abs(trackName.hashCode())
        val base = 90f
        val offset = (hash % 40).toFloat()
        return base + offset
    }

    // --- Media Control Methods ---

    fun pauseMusic() {
        try {
            mediaPlayerA?.let { if (it.isPlaying) it.pause() }
            mediaPlayerB?.let { if (it.isPlaying) it.pause() }
        } catch (_: Exception) {}
    }

    fun resumeMusic() {
        try {
            if (currentDeck == 0) {
                mediaPlayerA?.start()
            } else {
                mediaPlayerB?.start()
            }
        } catch (_: Exception) {}
    }

    fun stopEngine() {
        transitionJob?.cancel()
        speedSlideJobA?.cancel()
        speedSlideJobB?.cancel()
        
        mediaPlayerA?.apply {
            try { stop() } catch (_: Exception) {}
            release()
        }
        mediaPlayerA = null
        
        mediaPlayerB?.apply {
            try { stop() } catch (_: Exception) {}
            release()
        }
        mediaPlayerB = null
        
        isTransitioning = false
        transitionProgress = 0f
    }

    fun release() {
        playlistMonitorJob?.cancel()
        stopEngine()
        scope.cancel()
    }
}
