package com.example.fall_detection_app

import android.content.Context
import android.media.MediaPlayer

object AlarmUtils {
    private var mediaPlayer: MediaPlayer? = null

    fun playAlarmSound(context: Context) {
        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }

    fun stopAlarmSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
