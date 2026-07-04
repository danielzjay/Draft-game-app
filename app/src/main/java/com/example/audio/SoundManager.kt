package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import com.example.R

/**
 * Real audio playback — replaces the old fake "Now Playing" label that never made a sound.
 *
 * Short one-shot effects (coin, unlock, defeat, etc.) go through SoundPool, which is built for
 * exactly this: low-latency playback of many short clips. Background music uses a looping
 * MediaPlayer, which is the right tool for a single long-running track instead.
 *
 * Bundled tracks live in res/raw as .ogg (Vorbis compresses far better than the original .wav
 * files without an audible quality loss for game audio — the whole music+SFX bundle is now
 * ~630KB instead of ~11.5MB). A player can also pick their OWN song from their device (see
 * `playCustomMusic`) — that one is streamed by URI rather than bundled as a resource.
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
    private var musicPlayer: MediaPlayer? = null
    private var appContext: Context? = null

    var musicVolume: Float = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
            musicPlayer?.setVolume(field, field)
        }
    var sfxVolume: Float = 0.8f

    fun init(context: Context) {
        if (soundPool != null) return // already initialized
        appContext = context.applicationContext

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
        stopMusic()
        musicPlayer = MediaPlayer.create(context, resId)?.apply {
            isLooping = true
            setVolume(musicVolume, musicVolume)
            start()
        }
    }

    /**
     * Plays a song the player picked themselves from their own device (see MusicPicker.kt for
     * how the URI is obtained). Requires a persistable URI permission to survive app restarts —
     * that's requested at pick-time, not here.
     */
    fun playCustomMusic(context: Context, uri: Uri): Boolean {
        stopMusic()
        return try {
            musicPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                isLooping = true
                setVolume(musicVolume, musicVolume)
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            musicPlayer = null
            false
        }
    }

    fun pauseMusic() {
        musicPlayer?.let { if (it.isPlaying) it.pause() }
    }

    fun resumeMusic() {
        musicPlayer?.let { if (!it.isPlaying) it.start() }
    }

    fun stopMusic() {
        musicPlayer?.apply {
            try {
                stop()
            } catch (_: Exception) { /* not started yet — nothing to stop */ }
            release()
        }
        musicPlayer = null
    }

    fun release() {
        stopMusic()
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
}
