package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import com.example.R

/**
 * Real audio playback manager.
 * Short one-shot sound effects (coin, unlock, etc.) run through SoundPool for ultra-low latency.
 * Background soundtracks are delegated to the AutoDJEngine for professional continuous mixing,
 * crossfading, and tempo synchronization.
 */
object SoundManager {

    enum class Sfx(val resId: Int) {
        NOTIFICATION(R.raw.sfx_notification),
        VICTORY_FANFARE(R.raw.sfx_victory_fanfare),
        MATCH_COMPLETE(R.raw.sfx_match_complete),
        DEFEAT(R.raw.sfx_defeat),
        PURCHASE(R.raw.sfx_purchase),
        COIN(R.raw.sfx_coin),
        UNLOCK(R.raw.sfx_unlock)
    }

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<Sfx, Int>()
    private var appContext: Context? = null

    var musicVolume: Float = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
            AutoDJEngine.masterVolume = field
        }
    var sfxVolume: Float = 0.8f

    fun init(context: Context) {
        if (soundPool != null) return // already initialized
        appContext = context.applicationContext

        // Initialize AutoDJ engine
        AutoDJEngine.init(context)

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val pool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()
        soundPool = pool

        Sfx.entries.forEach { sfx ->
            soundIds[sfx] = pool.load(context, sfx.resId, 1)
        }
    }

    fun playSfx(sfx: Sfx) {
        val pool = soundPool ?: return
        val id = soundIds[sfx] ?: return
        pool.play(id, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    /** Plays one of the bundled background tracks, looping, replacing whatever was playing. */
    fun playBundledMusic(resId: Int) {
        val context = appContext ?: return
        val displayName = when (resId) {
            R.raw.music_battle_loop -> "Vanguard Anthem"
            else -> "Bundled Track"
        }
        AutoDJEngine.playBundledMusic(context, resId, displayName)
    }

    /** Plays custom songs using a continuous DJ playlist. */
    fun playCustomPlaylist(context: Context, uris: List<Uri>, startIndex: Int = 0) {
        val names = uris.map { uri ->
            uri.lastPathSegment ?: "Custom Song"
        }
        AutoDJEngine.playPlaylist(context, uris, names, startIndex)
    }

    /** Plays a custom song using a single track source. */
    fun playCustomMusic(context: Context, uri: Uri): Boolean {
        val name = uri.lastPathSegment ?: "Custom Track"
        AutoDJEngine.playSingleTrack(context, uri, name)
        return true
    }

    fun setPlaybackSpeed(speed: Float) {
        // Simple adapter for retro compatibility
        if (speed in 0.9f..1.1f) {
            AutoDJEngine.deckAPitchShift = 0f
            AutoDJEngine.deckBPitchShift = 0f
        } else {
            val delta = speed - 1.0f
            AutoDJEngine.deckAPitchShift = delta
            AutoDJEngine.deckBPitchShift = delta
        }
    }

    fun toggleBeatOverlay(context: Context, active: Boolean) {
        AutoDJEngine.bassSwapActive = active
    }

    fun pauseMusic() {
        AutoDJEngine.pauseMusic()
    }

    fun resumeMusic() {
        AutoDJEngine.resumeMusic()
    }

    fun stopMusic() {
        AutoDJEngine.stopEngine()
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundIds.clear()
        AutoDJEngine.release()
    }
}
