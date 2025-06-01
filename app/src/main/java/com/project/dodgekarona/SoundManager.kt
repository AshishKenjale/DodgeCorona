package com.project.dodgekarona

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import java.util.*

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
object SoundManager {
    val GAME_MUSIC: Int = R.raw.backgroundloop
    val PLAYER_DEATH = R.raw.playerinfected
    val PLAYER_POWER_UP = R.raw.playerinvincible
    private val sounds: MutableMap<Int, Int> = HashMap()
    private val songs: MutableMap<Int, MediaPlayer?> = HashMap()
    private val vols: MutableMap<MediaPlayer?, Float> = HashMap()
    private const val MAX_MUSIC_VOLUME = 0.5f
    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private var soundPool = SoundPool.Builder()
        .setAudioAttributes(attributes)
        .build()

    fun init(context: Context?) {
        var sound = PLAYER_DEATH
        var soundId = soundPool.load(context, sound, 1)
        sounds[sound] = soundId

        sound = PLAYER_POWER_UP
        soundId = soundPool.load(context, sound, 1)
        sounds[sound] = soundId
    }

    fun playSound(sound: Int) {
        println("Sound")
        val soundId = sounds[sound]
        soundPool.play(soundId!!, 1f, 1f, 0, 0, 1f)
    }

    fun playSound(sound: Int, context: Context?) {
        println("Sound")
        var soundId = sounds[sound]
        if (soundId == null) {
            println("New Sound")
            soundId = soundPool.load(context, sound, 1)
            sounds[sound] = soundId
        }
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    fun startSongLoop(song: Int, context: Context?) {
        println("Start Song Loop")
        var mp = songs[song]
        if (mp == null) {
            println("New Song")
            mp = MediaPlayer.create(context, song)
            songs[song] = mp
            vols[mp] = MAX_MUSIC_VOLUME
            mp.setVolume(MAX_MUSIC_VOLUME, MAX_MUSIC_VOLUME)
        }
        if (!mp!!.isPlaying) {
            if (!mp.isLooping) mp.isLooping = true
            mp.start()
        }
        for (prevMP in songs.values) {
            if (prevMP!!.isPlaying && prevMP !== mp) {
                val timer = Timer(true)
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        prevMP.setVolume(
                            vols[prevMP]!!,
                            vols[prevMP]!!
                        )
                        //Cancel and Purge the Timer if the desired volume has been reached
                        decrementVolume(prevMP)
                        println("decremented")
                        if (vols[prevMP]!! < 0.1f) {
                            println("terminating")
                            vols[prevMP] = MAX_MUSIC_VOLUME
                            prevMP.setVolume(
                                vols[prevMP]!!,
                                vols[prevMP]!!
                            )
                            prevMP.pause()
                            timer.cancel()
                            timer.purge()
                        }
                    }
                }
                timer.schedule(timerTask, 55, 55)
            }
        }
    }

    private fun decrementVolume(prevMP: MediaPlayer?) {
        vols[prevMP] = vols[prevMP]!! - 0.05f
    }

    fun endSongLoop(song: Int) {
        val mp = songs[song]
        if (mp != null && mp.isPlaying) {
            mp.pause()
        }
    }
}