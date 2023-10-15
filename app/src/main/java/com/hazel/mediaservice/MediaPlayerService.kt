package com.hazel.mediaservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class MediaPlayerService: Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.calm_down)
        mediaPlayer.setOnCompletionListener {
            scheduleMusicCompletionNotification()
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }
    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }
    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }
    fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    fun getTotalDuration(): Int {
        return mediaPlayer.duration
    }
    fun seekTo(newPosition: Int) {
        mediaPlayer.seekTo(newPosition)
    }
    private fun scheduleMusicCompletionNotification() {
        val workRequest = OneTimeWorkRequest.Builder(MusicCompletionWorker::class.java).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

}